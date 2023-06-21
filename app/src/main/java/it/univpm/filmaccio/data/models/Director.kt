package it.univpm.filmaccio.data.models

import com.google.gson.annotations.SerializedName

// Data class che rappresenta un regista, in questo caso serve solo per memorizzare i dati principali del regista da mostrare nella lista dei registi di un film/serie TV
data class Director(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("job") val job: String
)