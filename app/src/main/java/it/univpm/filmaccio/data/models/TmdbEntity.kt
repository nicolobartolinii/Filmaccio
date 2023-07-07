package it.univpm.filmaccio.data.models

import com.google.gson.annotations.SerializedName

// Data class che ci serve per la gestione della funzionalità di ricerca. Nella classe SearchResponse
// abbiamo una lista di TmdbEntity. Questo perché la ricerca può restituire film, serie TV e persone.
// In questa classe quindi memorizziamo i dati principali di queste "entità" relative a TMDB e in più
// ci aggiungiamo il tipo così quando un utente clicca su una di queste "entità" possiamo far
// avviare l'activity giusta in base al tipo.
data class TmdbEntity(
    @SerializedName("id") val id: Long,
    @SerializedName(value = "title", alternate = ["name"]) val title: String,
    @SerializedName(value = "poster_path", alternate = ["profile_path"]) val imagePath: String?,
    @SerializedName("media_type") val mediaType: String
) : java.io.Serializable