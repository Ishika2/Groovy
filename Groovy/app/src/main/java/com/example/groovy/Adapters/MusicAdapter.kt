package com.example.groovy.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.groovy.Modes.SongModel
import com.example.groovy.R

class MusicAdapter(var songList: ArrayList<SongModel>, var context: Context) :
    RecyclerView.Adapter<MusicAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var song_title: TextView = itemView.findViewById(R.id.song_title)
        var song_artist: TextView = itemView.findViewById(R.id.song_artist)
        var song_image: ImageView = itemView.findViewById(R.id.song_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.music_layout, parent, false)
        )
    }

    @SuppressLint("SyntheticAccessor")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = songList[position]
        holder.song_title.text = songList[position].song_title
        holder.song_artist.text = songList[position].artist
        var bitmap: Bitmap?=null
        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver,songList[position].image)
            holder.song_image.setImageBitmap(bitmap)
        }
        catch(e: Exception)
        {

        }
    }

    override fun getItemCount(): Int {
        return songList.size;
    }
}