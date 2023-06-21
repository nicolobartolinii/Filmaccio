package it.univpm.filmaccio.data.models

import com.google.firebase.Timestamp

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
    var birthDate: Timestamp = Timestamp.now()
)