package it.univpm.filmaccio.data.models

import com.google.gson.annotations.SerializedName

/**
 * Data class che rappresenta un personaggio, in questo caso serve solo per memorizzare i dati
 * principali del personaggio da mostrare nella lista dei personaggi di un film/serie TV.
 * In questa classe e in gran parte delle altre classi models, usiamo @SerializedName per
 * specificare il nome del campo JSON che vogliamo mappare con il campo della classe.
 * Questa annotazione permette di convertire il risultato JSON della chiamata all'API in un oggetto
 * Kotlin, in modo da poter accedere ai dati tramite i campi della classe.
 *
 * @param id id del personaggio
 * @param name nome dell'attore
 * @param profilePath path dell'immagine dell'attore
 * @param character nome del personaggio
 *
 * @author nicolobartolinii
 */
data class Character(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("profile_path") val profilePath: String?,
    @SerializedName("character") val character: String,
) : java.io.Serializable