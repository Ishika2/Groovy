package com.example.groovy.Services

import android.annotation.SuppressLint
import android.app.Service
import android.content.ContentUris
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.os.PowerManager
import android.provider.MediaStore
import android.widget.SeekBar
import android.widget.TextView
import com.example.groovy.Modes.SongModel
import java.util.concurrent.TimeUnit

@SuppressLint("Registered")
class MusicService : Service(), MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener,
    MediaPlayer.OnErrorListener {
    lateinit var player: MediaPlayer
    lateinit var songs: SongModel
    private val musicBind = MusicBinder()
    lateinit var seekBar: SeekBar
    private val interval = 1000
    lateinit var start_point: TextView
    lateinit var end_point: TextView
    override fun onBind(p0: Intent?): IBinder? {
        return musicBind
    }

    override fun onCreate() {
        super.onCreate()

        player = MediaPlayer()
        initMusic()
    }

    fun initMusic() {
        player.setWakeMode(applicationContext, PowerManager.PARTIAL_WAKE_LOCK)
        player.setAudioStreamType(AudioManager.STREAM_MUSIC)
        player.setOnPreparedListener(this)
        player.setOnCompletionListener(this)
        player.setOnErrorListener(this)
    }


    override fun onUnbind(intent: Intent?): Boolean {
        player.stop()
        player.reset()
        player.release()
        return false

    }

    inner class MusicBinder : Binder() {
        val service: MusicService
            get() = this@MusicService
    }

    companion object {
        const val STOPPED = 0
        const val PAUSED = 1
        const val PLAYING = 2
    }

    override fun onPrepared(p0: MediaPlayer?) {
        p0!!.start()
        val duration = p0.duration
        seekBar.max = duration
        seekBar.postDelayed(progressRunner, interval.toLong())
        end_point.text = String.format(
            "%d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(duration.toLong()),
            TimeUnit.MILLISECONDS.toMinutes(duration.toLong())
        )
    }

    private fun playSong() {
        player.reset()
        val playSong = songs
        val current_songId = playSong.song_id
        val trackUri =
            ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, current_songId)

        player.setDataSource(applicationContext, trackUri)
        player.prepareAsync()
        progressRunner.run()
    }

    fun setUI(seekBar: SeekBar, start_int: TextView, end_int: TextView) {
        this.seekBar = seekBar
        start_point = start_int
        end_point = end_int
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if(p2)
                {
                    player.seekTo(p1)
                }
                start_point.text = String.format(
                    "%d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(p1.toLong()),
                    TimeUnit.MILLISECONDS.toMinutes(p1.toLong())
                )

            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }

        })
    }

    private val progressRunner: Runnable = object : Runnable {
        override fun run() {
            seekBar.progress = player.currentPosition
            if (player.isPlaying) {
                seekBar.postDelayed(this, interval.toLong())
            }
        }
    }

    override fun onCompletion(p0: MediaPlayer?) {

    }

    override fun onError(p0: MediaPlayer?, p1: Int, p2: Int): Boolean {
        return false
    }

}