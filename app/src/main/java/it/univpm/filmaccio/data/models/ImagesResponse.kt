package it.univpm.filmaccio.data.models

import com.google.gson.annotations.SerializedName

data class ImagesResponse(
    @SerializedName("backdrops") val backdrops: List<Backdrop>
) {
    data class Backdrop(
        @SerializedName("file_path") val filePath: String,
        @SerializedName("iso_639_1") val iso6391: String?,
        @SerializedName("vote_average") val voteAverage: Double,
        @SerializedName("vote_count") val voteCount: Int
    )
}