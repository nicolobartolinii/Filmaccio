package it.univpm.filmaccio.data.models

import com.google.gson.annotations.SerializedName

/**
 * Data class che contiene i dati principali di un film, serie TV o persona.
 * Questa classe viene usata nella schermata di ricerca (nello specifico nella recycler view dei risultati della ricerca)
 * e anche in altre schermate in cui possono essere mostrati dati multipli.
 * In questa classe memorizziamo i dati principali di queste "entità" relative a TMDB e in più
 * ci aggiungiamo il tipo così quando un utente clicca su una di queste "entità" possiamo far
 * avviare l'activity giusta in base al tipo.
 *
 * @param id id dell'entità
 * @param title titolo dell'entità
 * @param imagePath path dell'immagine dell'entità
 * @param mediaType tipo dell'entità (film, serie TV o persona)
 *
 * @author nicolobartolinii
 */
data class TmdbEntity(
    @SerializedName("id") val id: Long,
    @SerializedName(value = "title", alternate = ["name"]) val title: String,
    @SerializedName(value = "poster_path", alternate = ["profile_path"]) val imagePath: String?,
    @SerializedName("media_type") val mediaType: String
) : java.io.Serializable