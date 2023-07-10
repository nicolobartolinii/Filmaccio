package it.univpm.filmaccio.data.models

import java.io.Serializable

/**
 * Data class che rappresenta una recensione di un utente.
 *
 * @param user utente che ha scritto la recensione
 * @param review testo della recensione
 * @param date data della recensione
 *
 * @author nicolobartolinii
 */
data class ReviewTriple(val user: User, val review: String, val date: String) : Serializable
