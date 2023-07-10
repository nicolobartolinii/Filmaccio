package it.univpm.filmaccio.data.models

import com.google.gson.annotations.SerializedName

/**
 * Data class che rappresenta la risposta JSON della chiamata all'endpoint search/multi.
 * Questo endpoint restituisce una lista di film, serie TV e persone in base ad una query
 * specificata dall'utente.
 *
 * @param entities lista di film, serie TV e persone
 * @param page numero di pagina della risposta dell'API
 * @param totalPages numero totale di pagine della risposta dell'API
 * @param totalResults numero totale di risultati della risposta dell'API
 *
 * @author nicolobartolinii
 */
data class SearchResponse(
    @SerializedName("results") val entities: List<TmdbEntity>,
    @SerializedName("page") val page: Int,
    @SerializedName("total_pages") val totalPages: Int,
    @SerializedName("total_results") val totalResults: Int
)