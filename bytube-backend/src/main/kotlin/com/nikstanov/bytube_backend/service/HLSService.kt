package com.nikstanov.bytube_backend.service

import java.io.File

interface HLSService {
    fun generateMasterPlaylist(playlists: List<String>, outputDir: File): File
    fun getVideoResolution(file: File): Int
    fun generateHLS(
        inputFile: File,
        outputDir: File,
        height: String,
        bitrate: Int
    ): String
}