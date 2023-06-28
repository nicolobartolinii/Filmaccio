package it.univpm.filmaccio.data.models

import com.google.gson.annotations.SerializedName

data class NextEpisode(
    val seriesId: Long,
    val seriesTitle: String,
    val seasonNumber: Long,
    val episodeNumber: Long,
    val episodeTitle: String,
    val posterPath: String?
)
