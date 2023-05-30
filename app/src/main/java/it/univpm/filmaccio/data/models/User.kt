package it.univpm.filmaccio.data.models

import com.google.firebase.Timestamp

data class User(
    var uid: String = "",
    var username: String = "",
    var email: String = "",
    var profileImage: String = "",
    var nameShown: String = "",
    var gender: String = "",
    var birthDate: Timestamp = Timestamp.now()
)