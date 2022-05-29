package com.example.groovy

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.ContentUris
import android.content.ServiceConnection
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.example.groovy.Adapters.MusicAdapter
import com.example.groovy.Modes.SongModel
import com.example.groovy.Services.MusicService

class MainActivity : AppCompatActivity() {
    lateinit var list: ArrayList<SongModel>
    lateinit var adapter: MusicAdapter
    lateinit var musicService: MusicService
    lateinit var seekBar: SeekBar
    lateinit var start_tv: TextView
    lateinit var end_tv: TextView

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        list = ArrayList()

        if(!Python.isStarted()) {
            Python.start(AndroidPlatform(this))   //python started
        }

        var py:Python = Python.getInstance()  //python instance
        var pyobj:PyObject = py.getModule("model.py") //python object

        var obj:PyObject = pyobj.callAttr("main") //calling function

        //display the text

        var manager = LinearLayoutManager(applicationContext)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        recyclerView.layoutManager = manager
        adapter = MusicAdapter(list, applicationContext)
        recyclerView.adapter = adapter
        lateinit var bottomLayout: LinearLayout


        val searchText = findViewById<EditText>(R.id.search_edittext)

        searchText.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                searchSong(p0.toString())

            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })
        //recyclerView.adapter = MusicAdapter(list,applicationContext)
        //recyclerview.adapter = MusicAdapter()
        getSongs()
    }

    //fetch music
    @SuppressLint("NotifyDataSetChanged")
    private fun getSongs() {
        list.clear()
        val contentResolver = this.contentResolver
        var songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        var cursor = contentResolver.query(songUri, null, null, null, null)
        if (cursor != null && cursor.moveToFirst()) {
            val songId = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val songTitle = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val songArtist = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val songData = cursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            val date = cursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)
            val albumColumnn = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)
            while (cursor.moveToNext()) {
                val currentId = cursor.getLong(songId)
                var song_Title = cursor.getString(songTitle)
                var song_artist = cursor.getString(songArtist)
                var song_data = cursor.getString(songData)
                var song_date = cursor.getString(date)
                var albumId = cursor.getLong(albumColumnn)

                val IMAGE_URI = Uri.parse("content://media/external/audio/albumart")
                val album_uri = ContentUris.withAppendedId(IMAGE_URI, albumId)

                if (!song_artist.equals("<unknown>")) {
                    list.add(
                        SongModel(
                            currentId,
                            song_Title,
                            song_artist,
                            song_data,
                            song_date,
                            album_uri
                        )
                    )
                }
            }
            adapter.notifyDataSetChanged()
        }
    }
    //search song
    private fun searchSong (value:String)
    {
        var songList = ArrayList<SongModel>()
        for(song in list)
        {
            var islist_added = false
            if(song.song_title.toLowerCase().contains(value.toLowerCase()))
            {
                songList.add(song)
                islist_added = true
            }
            if(song.artist.toLowerCase().contains(value.toLowerCase()))
            {
                if(!islist_added)
                songList.add(song)
            }
        }
        adapter.updateList(songList)
    }

    private var musicConnection : ServiceConnection = object : ServiceConnection
    {
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            val binder: MusicService.MusicBinder = p1 as MusicService.MusicBinder

            musicService = binder.service
            //musicService.setUI(bottom_layout.seekbar,bottom_layout)
        }

        override fun onServiceDisconnected(p0: ComponentName?) {

        }

    }
}