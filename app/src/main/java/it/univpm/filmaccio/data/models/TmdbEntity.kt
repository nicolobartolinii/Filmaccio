package it.univpm.filmaccio.data.models

import com.google.gson.annotations.SerializedName

data class TmdbEntity(
    @SerializedName("id") val id: Int,
    @SerializedName(value = "title", alternate = ["name"]) val title: String,
    @SerializedName(value = "poster_path", alternate = ["profile_path"]) val imagePath: String?,
    @SerializedName("media_type") val mediaType: String
)