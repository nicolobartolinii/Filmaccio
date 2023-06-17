package it.univpm.filmaccio.data.models

import com.google.gson.annotations.SerializedName

data class Movie(
    @SerializedName("id") val id: Long,
    @SerializedName("title") val title: String,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("overview") val overview: String,
    @SerializedName("release_date") val releaseDate: String,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("runtime") val duration: Int,
    @SerializedName("credits") var credits: Credits,
) {
    data class Credits (
        @SerializedName("cast") var cast: List<Character>,
        @SerializedName("crew") var crew: List<Director>
    )
}