package com.example.domain.model

import java.net.URL

data class Song(
    val id: String,
    val songName: String,
    val albumName: String,
    val albumArt: String?
)