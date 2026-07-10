package com.example.playlistmaker

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

class TracksViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    private val trackName: TextView = itemView.findViewById(R.id.track_name)
    private val artistName: TextView = itemView.findViewById(R.id.artist_name)
    private val trackTime: TextView = itemView.findViewById(R.id.track_time)
    private val coverImage: ImageView = itemView.findViewById(R.id.track_image)

    // Кешируем SimpleDateFormat один раз на уровне ViewHolder
    private val dateFormat by lazy {
        SimpleDateFormat("mm:ss", Locale.getDefault())
    }

    fun bind(model: Track) {
        trackName.text = model.trackName
        artistName.text = model.artistName
        trackTime.text = dateFormat.format(model.trackTimeMillis)

        val roundingRadius = itemView.context.resources
            .getDimensionPixelSize(R.dimen.track_cover_rounding)
        Glide.with(itemView)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.ic_album_image_placeholder_45)
            .centerCrop()
            .transform(RoundedCorners(roundingRadius))
            .into(coverImage)
    }
}