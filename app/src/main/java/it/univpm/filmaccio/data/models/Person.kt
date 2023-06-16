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
    @SerializedName("place_of_death") val placeOfDeath: String?,
    @SerializedName("combined_credits") var combinedCredits: CombinedCredits
) {
    var products: List<Product> = listOf()
    data class CombinedCredits(
        @SerializedName("cast") val cast: List<Product>,
        @SerializedName("crew") val crew: List<Product>
    )

    data class Product(
        @SerializedName("id") val id: Int,
        @SerializedName("title", alternate = ["name"]) val title: String,
        @SerializedName("poster_path") val posterPath: String?,
        @SerializedName("media_type") val mediaType: String,
        @SerializedName("popularity") val popularity: Double,
    )
}