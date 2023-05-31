package it.univpm.filmaccio.data.models

import com.google.gson.annotations.SerializedName

data class DiscoverSeriesResponse(
    @SerializedName("results") val series: List<Series>,
    @SerializedName("page") val page: Int,
    @SerializedName("total_pages") val totalPages: Int,
    @SerializedName("total_results") val totalResults: Int
)