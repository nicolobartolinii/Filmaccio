package it.univpm.filmaccio.data.models

import com.google.gson.annotations.SerializedName

data class Character(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("profile_path") val profilePath: String?,
    @SerializedName("character") val character: String,
)