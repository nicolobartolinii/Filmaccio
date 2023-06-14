package it.univpm.filmaccio.data.models

import com.google.gson.annotations.SerializedName

data class Director(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("job") val job: String
)