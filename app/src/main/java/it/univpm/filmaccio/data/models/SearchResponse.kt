package it.univpm.filmaccio.data.models

import com.google.gson.annotations.SerializedName

// Data class che si occupa di gestire il risultato alla chiamata all'endpoint search/multi che
// cerca film, serie TV e persone in base ad una query specificata dall'utente.
data class SearchResponse(
    @SerializedName("results") val entities: List<TmdbEntity>,
    @SerializedName("page") val page: Int,
    @SerializedName("total_pages") val totalPages: Int,
    @SerializedName("total_results") val totalResults: Int
)