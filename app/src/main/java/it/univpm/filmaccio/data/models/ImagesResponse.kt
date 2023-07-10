package it.univpm.filmaccio.data.models

import com.google.gson.annotations.SerializedName
import it.univpm.filmaccio.data.models.ImagesResponse.Backdrop

/**
 * Data class che rappresenta la risposta JSON della chiamata all'endpoint .../images.
 * Questo endpoint restituisce una lista di immagini di un film/serie TV.
 *
 * @param backdrops lista di immagini
 *
 * @see Backdrop
 *
 * @author nicolobartolinii
 */
data class ImagesResponse(
    @SerializedName("backdrops") val backdrops: List<Backdrop>
) {

    /**
     * Data class che rappresenta un'immagine di un film/serie TV.
     *
     * @param filePath path dell'immagine
     * @param iso6391 codice ISO 639-1 della lingua dell'immagine
     * @param voteAverage media dei voti dell'immagine
     * @param voteCount numero di voti dell'immagine
     *
     * @author nicolobartolinii
     */
    data class Backdrop(
        @SerializedName("file_path") val filePath: String,
        @SerializedName("iso_639_1") val iso6391: String?,
        @SerializedName("vote_average") val voteAverage: Double,
        @SerializedName("vote_count") val voteCount: Int
    )
}