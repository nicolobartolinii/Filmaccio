package it.univpm.filmaccio.data.models

import com.google.firebase.Timestamp
import java.io.Serializable

/**
 * Data class che rappresenta un utente dell'applicazione.
 *
 * @param uid uid dell'utente
 * @param username username dell'utente
 * @param email email dell'utente
 * @param profileImage path dell'immagine del profilo dell'utente
 * @param nameShown nome visualizzato dell'utente
 * @param gender genere dell'utente
 * @param birthDate data di nascita dell'utente
 * @param backdropImage path dell'immagine di sfondo dell'utente
 * @param movieMinutes minuti totali di film visti dall'utente
 * @param moviesNumber numero totale di film visti dall'utente
 * @param tvMinutes minuti totali di serie TV viste dall'utente
 * @param tvNumber numero totale di episodi visti dall'utente
 *
 * @author nicolobartolinii
 */
data class User(
    var uid: String = "",
    var username: String = "",
    var email: String = "",
    var profileImage: String = "",
    var nameShown: String = "",
    var gender: String = "",
    @Transient var birthDate: Timestamp = Timestamp.now(),
    var backdropImage: String = "https://firebasestorage.googleapis.com/v0/b/filmaccio.appspot.com/o/desert.jpg?alt=media&token=a2f60711-b962-40f9-9a8f-1b948e1cd92e",
    val movieMinutes: Long = 0,
    val moviesNumber: Long = 0,
    val tvMinutes: Long = 0,
    val tvNumber: Long = 0
) : Serializable