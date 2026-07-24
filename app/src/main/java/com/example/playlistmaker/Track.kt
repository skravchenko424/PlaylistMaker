package com.example.playlistmaker

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.Locale

@Parcelize
data class Track(
    val trackId: Long, // Уникальный идентификатор трека в iTunes Store
    val trackName: String, // Название композиции
    val artistName: String, // Имя исполнителя
    val trackTimeMillis: Long, // Продолжительность трека
    val artworkUrl100: String, // Ссылка на изображение обложки
    val collectionName: String? = null, // Название альбома
    val releaseDate: String? = null, // Дата релиза (в формате ISO 8601)
    val primaryGenreName: String? = null, // Жанр трека
    val country: String? = null // Страна исполнителя
) : Parcelable {

    // Вспомогательное свойство для получения года из releaseDate
    val year: String?
        get() {
            return try {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
                val date = dateFormat.parse(releaseDate ?: return null)
                SimpleDateFormat("yyyy", Locale.getDefault()).format(date ?: return null)
            } catch (e: Exception) {
                null
            }
        }
}