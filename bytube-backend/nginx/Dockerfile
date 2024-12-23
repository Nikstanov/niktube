# Use the official nginx base image
FROM tiangolo/nginx-rtmp

# Install required tools for the upload script
RUN apt-get update && apt-get install -y \
    curl \
    inotify-tools \
    && apt-get clean

# Copy NGINX configuration
COPY nginx.conf /etc/nginx/nginx.conf

# Copy the MinIO upload script
COPY upload_to_minio.sh /usr/local/bin/upload_to_minio.sh
RUN chmod +x /usr/local/bin/upload_to_minio.sh

# Expose ports for RTMP and HTTP
EXPOSE 1935 8080

# Start NGINX and upload script
CMD ["sh", "-c", "nginx && /usr/local/bin/upload_to_minio.sh"]
