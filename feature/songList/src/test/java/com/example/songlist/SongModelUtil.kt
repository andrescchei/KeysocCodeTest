package com.example.songlist

import com.example.domain.model.Song

object SongModelUtil {
    fun createSong(songName: String, albumName: String) = Song(
        id = "",
        songName = songName,
        albumName = albumName,
        albumArt = null
    )
}