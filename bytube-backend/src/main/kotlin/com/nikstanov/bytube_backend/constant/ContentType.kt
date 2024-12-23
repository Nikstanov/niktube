package com.nikstanov.bytube_backend.constant

enum class ContentType {
    M3U8, TS;

    companion object {
        const val APPLE = "application/vnd.apple.mpegurl"
        const val IMAGE = "image/png"

        fun getContentType(fileName: String): String {
            return when {
                fileName.endsWith(".m3u8") -> "application/vnd.apple.mpegurl"
                fileName.endsWith(".ts") -> "video/mp2t"
                else -> "application/octet-stream"
            }
        }
    }
}