package it.univpm.filmaccio.data.models

import com.google.firebase.Timestamp

data class User(val uid: String, val username: String, val email: String, val profileImageUrl: String, val nameShown: String, val birthDate: Timestamp)