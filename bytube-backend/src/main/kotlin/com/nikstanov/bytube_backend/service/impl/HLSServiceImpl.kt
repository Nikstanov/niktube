package com.nikstanov.bytube_backend.service.impl

import com.nikstanov.bytube_backend.service.HLSService
import org.springframework.stereotype.Service
import java.io.File
import java.util.regex.Pattern

@Service
class HLSServiceImpl : HLSService {
    override fun generateMasterPlaylist(playlists: List<String>, outputDir: File): File {
        val masterPlaylist = File(outputDir, "master.m3u8")
        masterPlaylist.writeText("#EXTM3U\n")

        playlists.forEach { playlist ->
            val resolution = when (playlist) {
                "1440p.m3u8" -> "2560x1440"
                "1080p.m3u8" -> "1920x1080"
                "720p.m3u8" -> "1280x720"
                "480p.m3u8" -> "854x480"
                else -> ""
            }
            val bandwidth = when (playlist) {
                "1440p.m3u8" -> 8000000
                "1080p.m3u8" -> 5000000
                "720p.m3u8" -> 3000000
                "480p.m3u8" -> 1500000
                else -> 0
            }

            masterPlaylist.appendText("#EXT-X-STREAM-INF:BANDWIDTH=$bandwidth,RESOLUTION=$resolution\n")
            masterPlaylist.appendText(playlist + "\n")
        }

        return masterPlaylist
    }

    override fun getVideoResolution(file: File): Int {
        val process = ProcessBuilder(
            "ffprobe",
            "-v", "error",
            "-select_streams", "v:0",
            "-show_entries", "stream=width,height",
            "-of", "csv=s=,:p=0",
            file.absolutePath
        ).redirectErrorStream(true).start()

        val output = process.inputStream.bufferedReader().readText()
        process.waitFor()

        if (process.exitValue() != 0) {
            throw RuntimeException("Failed to get video resolution: $output")
        }

        return output.trim().split(Pattern.compile(","),2)[1].toInt() // E.g., "1920,1080" -> "1080"
    }

    override fun generateHLS(
        inputFile: File,
        outputDir: File,
        height: String,
        bitrate: Int
    ): String {
        val playlistFile = File(outputDir, "${height}p.m3u8")
        val process = ProcessBuilder(
            "ffmpeg",
            "-i", inputFile.absolutePath,
            "-vf", "scale=-2:$height",
            "-c:a", "aac",
            "-b:v", "${bitrate}k",
            "-maxrate", "${bitrate * 2}k",
            "-bufsize", "${bitrate * 4}k",
            "-hls_time", "10",
            "-hls_playlist_type", "vod",
            "-hls_segment_filename", "${outputDir.absolutePath}/${height}p_%03d.ts",
            playlistFile.absolutePath
        ).redirectErrorStream(true).start()

        process.inputStream.bufferedReader().use { println(it.readText()) }
        process.waitFor()

        if (process.exitValue() != 0) {
            throw RuntimeException("Failed to generate HLS for $height")
        }

        return playlistFile.name
    }
}