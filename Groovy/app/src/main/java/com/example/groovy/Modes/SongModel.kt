package com.example.groovy.Modes

import android.net.Uri

class SongModel(
    var song_id: Long,
    var song_title: String,
    var artist: String,
    var songData: String,
    var date: String,
    var image: Uri
) {
}