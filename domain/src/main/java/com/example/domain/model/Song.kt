package com.example.domain.model

import java.net.URL

data class Song (
    val songName: String,
    val albumName: String,
    val albumArt: URL?
)