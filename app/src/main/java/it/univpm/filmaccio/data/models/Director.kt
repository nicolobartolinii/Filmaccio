package it.univpm.filmaccio.data.models

import com.google.gson.annotations.SerializedName

data class Director(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("job") val job: String
)