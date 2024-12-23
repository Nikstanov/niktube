#!/bin/bash

# MinIO settings
MINIO_ENDPOINT="http://minio:9000"
MINIO_BUCKET="streams"
MINIO_ACCESS_KEY="admin"
MINIO_SECRET_KEY="adminPassword"

# Install MinIO client
if ! command -v mc &> /dev/null; then
    curl -O https://dl.min.io/client/mc/release/linux-amd64/mc
    chmod +x mc
    mv mc /usr/local/bin/
fi

# Configure MinIO client
mc alias set myminio "$MINIO_ENDPOINT" "$MINIO_ACCESS_KEY" "$MINIO_SECRET_KEY"

# Function to upload a file to MinIO
upload_to_minio() {
    local file_path="$1"
    local file_name=$(basename "$file_path")

    echo "Uploading $file_name to MinIO..."
    if ! mc cp "$file_path" myminio/"$MINIO_BUCKET"/; then
        echo "Error uploading $file_name to MinIO" >> /var/log/minio_upload.log
    fi
}


# Watch for new or modified files in the HLS directory
inotifywait -m /tmp/hls -e close_write |
while read -r directory events filename; do
    file_path="$directory$filename"

    if [[ $filename == *.ts ]]; then
        stream_id=$(echo "$filename" | sed -E 's/-[0-9]+\.ts$//')

        # Upload .ts files immediately
        upload_to_minio "$file_path"

        # Construct the corresponding .m3u8 file name
        m3u8_file="${directory}${stream_id}.m3u8"

        # Check if the .m3u8 file exists and upload it
        if [[ -f "$m3u8_file" ]]; then
            echo "Detected corresponding .m3u8 file: $m3u8_file"
            upload_to_minio "$m3u8_file"
        else
            echo "Warning: .m3u8 file not found for streamId $stream_id" >> /var/log/minio_upload.log
        fi
    fi
done
