package it.univpm.filmaccio.data.models

import com.google.gson.annotations.SerializedName

data class SearchResponse(
    @SerializedName("results") val entities: List<TmdbEntity>,
    @SerializedName("page") val page: Int,
    @SerializedName("total_pages") val totalPages: Int,
    @SerializedName("total_results") val totalResults: Int
)