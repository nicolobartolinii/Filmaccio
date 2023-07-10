package it.univpm.filmaccio.data.models

/**
 * Data class che si occupa di memorizzare i dati principali di un prossimo episodio che l'utente
 * deve vedere di una serie TV in visione. Questa classe viene utilizzata nella schermata Episodi.
 *
 * @param seriesId id della serie TV
 * @param seriesTitle titolo della serie TV
 * @param seasonNumber numero della stagione
 * @param episodeNumber numero dell'episodio
 * @param episodeTitle titolo dell'episodio
 * @param posterPath path del poster della serie TV
 * @param duration durata dell'episodio
 *
 * @author nicolobartolinii
 */
data class NextEpisode(
    val seriesId: Long,
    val seriesTitle: String,
    val seasonNumber: Long,
    val episodeNumber: Long,
    val episodeTitle: String,
    val posterPath: String?,
    val duration: Long = 0
)