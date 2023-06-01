package it.univpm.filmaccio.data.models

import com.google.gson.annotations.SerializedName

data class Series(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("overview") val overview: String,
    @SerializedName("release_date") val releaseDate: String,
    @SerializedName("backdrop_path") val backdropPath: String?,
)