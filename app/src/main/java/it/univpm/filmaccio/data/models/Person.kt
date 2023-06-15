package it.univpm.filmaccio.data.models

import com.google.gson.annotations.SerializedName

data class Person(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("profile_path") val profilePath: String?,
    @SerializedName("biography") val biography: String,
    @SerializedName("birthday") var birthday: String?,
    @SerializedName("deathday") var deathday: String?,
    @SerializedName("place_of_birth") val placeOfBirth: String?,
    @SerializedName("gender") var gender: Int,
    @SerializedName("known_for_department") var knownFor: String,
    @SerializedName("place_of_death") val placeOfDeath: String?
)