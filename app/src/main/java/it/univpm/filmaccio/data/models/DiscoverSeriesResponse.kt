package it.univpm.filmaccio.data.models

import com.google.gson.annotations.SerializedName

// Stesso identico discorso fatto con DiscoverMoviesResponse ma per le serie TV

/**
 * Data class che rappresenta la risposta JSON della chiamata all'endpoint discover/tv.
 * Nota: guardare la documentazione di DiscoverMoviesResponse.
 *
 * @see DiscoverMoviesResponse
 *
 * @param series lista di serie TV
 *
 * @author nicolobartolinii
 */
data class DiscoverSeriesResponse(
    @SerializedName("results") val series: List<Series>,
)