package it.univpm.filmaccio.data.models

import com.google.gson.annotations.SerializedName

data class Series(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val title: String,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("overview") val overview: String,
    @SerializedName("first_air_date") val releaseDate: String,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("number_of_episodes") val duration: Int,
    @SerializedName("credits") var credits: Credits,
    @SerializedName("created_by") var creator: List<Director>,
    @SerializedName("seasons") var seasons: List<Season>
) {
    data class Credits(
        @SerializedName("cast") var cast: List<Character>,
        @SerializedName("crew") var crew: List<Director>
    )

    data class Season(
        @SerializedName("season_number") var number: Int,
        @SerializedName("air_date") var releaseDate: String?,
        @SerializedName("poster_path") var posterPath: String?,
        @SerializedName("overview") var overview: String,
        @SerializedName("episodes") var episodes: List<Episode> = emptyList(),
        @SerializedName("name") var name: String
    )

    data class Episode(
        @SerializedName("episode_number") val number: Int,
        @SerializedName("runtime") val duration: Int,
        @SerializedName("air_date") val releaseDate: String,
        @SerializedName("still_path") val imagePath: String?,
        @SerializedName("overview") val overview: String,
        @SerializedName("name") val name: String
    )
}