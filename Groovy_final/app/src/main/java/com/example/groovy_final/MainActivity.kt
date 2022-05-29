package com.example.groovy_final

import android.annotation.SuppressLint
import android.content.*
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.groovy_final.Adapters.MusicAdapter
import com.example.groovy_final.Services.MusicService
import com.example.groovy_final.`interface`.onSongSelect
import com.example.groovy_final.Modes.SongModel
import com.example.groovy_final.R
import java.time.temporal.TemporalAdjusters.next
import android.content.Intent as Intent

class MainActivity() : AppCompatActivity(), onSongSelect, View.OnClickListener {
    lateinit var list: ArrayList<SongModel>
    lateinit var adapter: MusicAdapter
    lateinit var musicService: MusicService
    lateinit var seekBar: SeekBar
    //private lateinit var playintent: Intent
    lateinit var songModel: SongModel

    @SuppressLint("WrongViewCast", "CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        list = ArrayList()

//        if(!Python.isStarted()) {
//            Python.start(AndroidPlatform(this))   //python started
//        }
//
//        var py:Python = Python.getInstance()  //python instance
//        var pyobj:PyObject = py.getModule("model.py") //python object
//
//        var obj:PyObject = pyobj.callAttr("main") //calling function

        //display the text

        val manager = LinearLayoutManager(applicationContext)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        recyclerView.layoutManager = manager
        adapter = MusicAdapter(list, applicationContext,this)
        recyclerView.adapter = adapter
        val playpause_btn = findViewById<ImageView>(R.id.playpause)
        playpause_btn.setOnClickListener(this)
        val prev_btn = findViewById<ImageView>(R.id.prev)
        prev_btn.setOnClickListener(this)
        val next_btn = findViewById<ImageView>(R.id.next)
        next_btn.setOnClickListener(this)
        var togglebtn: ImageView? = null
        togglebtn = findViewById(R.id.playpause)
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

    private fun handleKeyEvent(view: View?, keyCode: Int): Boolean {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            //Hide the keyboard
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            if (view != null) {
                inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
            }
            return true
        }
        return false
    }

    //fetch music
    @SuppressLint("NotifyDataSetChanged", "Recycle")
    private fun getSongs() {
        list.clear()
        val contentResolver = this.contentResolver
        val songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val cursor = contentResolver.query(songUri, null, null, null, null)
        if (cursor != null && cursor.moveToFirst()) {
            val songId = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val songTitle = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val songArtist = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val songData = cursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            val date = cursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)
            val albumColumnn = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)
            while (cursor.moveToNext()) {
                val currentId = cursor.getLong(songId)
                val song_Title = cursor.getString(songTitle)
                val song_artist = cursor.getString(songArtist)
                val song_data = cursor.getString(songData)
                val song_date = cursor.getString(date)
                val albumId = cursor.getLong(albumColumnn)

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
        val songList = ArrayList<SongModel>()
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

    override fun onStart()
    {
        super.onStart()
//        if(playintent == null)
//        {
//            playintent= Intent(this,MusicService::class.java)
//            bindService(playintent,musicConnection, Context.BIND_AUTO_CREATE)
//            startService(playintent)
//        }
    }

    override fun onDestroy() {
        //stopService(playintent)
        unbindService(musicConnection)
        super.onDestroy()
    }

    @SuppressLint("CutPasteId")
    private fun updateUI()
    {
        val song = findViewById<TextView>(R.id.song_title_bar)
        song.text=songModel.song_title
        val song_artist = findViewById<TextView>(R.id.song_title_bar)
        song_artist.text=songModel.artist
        val songImg = findViewById<ImageView>(R.id.song_image)
        var bitmap: Bitmap?=null
        try {
            bitmap = MediaStore.Images.Media.getBitmap(contentResolver,songModel.image)
            songImg.setImageBitmap(bitmap)
        }
        catch(e: Exception)
        {

        }
    }

    private var musicConnection : ServiceConnection = object : ServiceConnection
    {
        @SuppressLint("WrongViewCast")
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            val binder: MusicService.MusicBinder = p1 as MusicService.MusicBinder

            musicService = binder.service
            val seek = findViewById<SeekBar>(R.id.seekbar)
            val start_txt = findViewById<TextView>(R.id.start_text)
            val end_txt = findViewById<TextView>(R.id.end_text)
            musicService.setUI(seek,start_txt,end_txt)
        }

        override fun onServiceDisconnected(p0: ComponentName?) {

        }

    }

    override fun onSelect(song: SongModel) {
        musicService.setSong(song)
        songModel=song
        updateUI()
    }

    override fun onClick(p0: View?) {
        when(p0)
        {
//            toggle_btn ->
//            {
//                if(musicService.playerState == 2)
//                {
//                    //pause song
//                    toggle_btn.setBackgroundResource(R.drawable.play_icon)
//                    musicService.pauseSong()
//                }
//                else if(musicService.playerState == 1)
//                {
//                    //resume song
//                    toggle_btn.setBackgroundResource(R.drawable.pause)
//                    musicService.resumeSong()
//
//                }
//            }
//            prev_btn ->
//            {
//
//            }
//            next_btn ->
//            {
//
//            }

        }
    }
}