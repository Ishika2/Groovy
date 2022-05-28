package com.example.groovy

import android.annotation.SuppressLint
import android.content.ContentUris
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.groovy.Adapters.MusicAdapter
import com.example.groovy.Modes.SongModel

class MainActivity : AppCompatActivity() {
    lateinit var list: ArrayList<SongModel>
    lateinit var adapter: MusicAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        list = ArrayList()

        var manager = LinearLayoutManager(applicationContext)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        recyclerView.layoutManager = manager
        adapter = MusicAdapter(list,applicationContext)
        recyclerView.adapter = adapter
        //recyclerView.adapter = MusicAdapter(list,applicationContext)
        //recyclerview.adapter = MusicAdapter()
        getSongs()
    }

    //fetch music
    @SuppressLint("NotifyDataSetChanged")
    private fun getSongs()
    {
        list.clear()
        val contentResolver = this.contentResolver
        var songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        var cursor = contentResolver.query(songUri, null, null, null, null)
        if(cursor != null && cursor.moveToFirst())
        {
            val songId = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val songTitle = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val songArtist = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val songData = cursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            val date = cursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)
            val albumColumnn = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)
            while(cursor.moveToNext())
            {
                val currentId = cursor.getLong(songId)
                var song_Title = cursor.getString(songTitle)
                var song_artist = cursor.getString(songArtist)
                var song_data = cursor.getString(songData)
                var song_date = cursor.getString(date)
                var albumId = cursor.getLong(albumColumnn)

                val IMAGE_URI = Uri.parse("content://media/external/audio/albumart")
                val album_uri = ContentUris.withAppendedId(IMAGE_URI,albumId)

                if(!song_artist.equals("<unknown>"))
                {
                    list.add(SongModel(currentId,song_Title,song_artist,song_data,song_date,album_uri))
                }
            }
            adapter.notifyDataSetChanged()
        }
    }
}