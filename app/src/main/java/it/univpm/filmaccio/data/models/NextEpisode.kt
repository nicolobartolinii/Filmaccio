package it.univpm.filmaccio.data.models

data class NextEpisode(
    val seriesId: Long,
    val seriesTitle: String,
    val seasonNumber: Long,
    val episodeNumber: Long,
    val episodeTitle: String,
    val posterPath: String?,
    val duration: Long = 0
)