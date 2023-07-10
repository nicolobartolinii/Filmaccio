package it.univpm.filmaccio.data.models

import com.google.gson.annotations.SerializedName

/**
 * Data class che rappresenta un regista, in questo caso serve solo per memorizzare i dati
 * principali del regista da mostrare nella lista dei registi di un film/serie TV.
 *
 * Nota: in realtà con regista intendiamo un membro della crew. Poi nel codice andiamo ad utilizzare
 * il parametro job per filtrare solo i registi/creatori.
 *
 * @param id id del regista
 * @param name nome del membro della crew
 * @param job ruolo del membro della crew
 *
 * @author nicolobartolinii
 */
data class Director(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("job") val job: String
) : java.io.Serializable