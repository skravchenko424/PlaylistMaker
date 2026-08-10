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

    val year: String? = parseYear(releaseDate)

    companion object {
        private val INPUT_DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        private val OUTPUT_YEAR_FORMAT = SimpleDateFormat("yyyy", Locale.getDefault())
        const val TRACK_EXTRA_NAME = "track"

        private fun parseYear(releaseDate: String?): String? {
            return try {
                val date = INPUT_DATE_FORMAT.parse(releaseDate ?: return null)
                OUTPUT_YEAR_FORMAT.format(date ?: return null)
            } catch (e: Exception) {
                null
            }
        }
    }
}