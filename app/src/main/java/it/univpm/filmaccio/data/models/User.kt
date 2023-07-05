package it.univpm.filmaccio.data.models

import com.google.firebase.Timestamp
import java.io.Serializable

// Data class che rappresenta un utente dell'applicazione. Questa classe ci serve sia per
// mostrare gli utenti nella funzionalità di ricerca che per mostrari le informazioni
// di altri utenti nelle schermate di profilo (non ancora implementate per utenti diversi da quello attuale).
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