package it.univpm.filmaccio.data.models

import java.io.Serializable

data class ReviewTriple(val user: User, val review: String, val date: String) : Serializable
