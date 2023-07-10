package it.univpm.filmaccio.main.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

/**
 * Questo oggetto singleton si occupa di gestire tutte le operazioni che riguardano Firebase Auth.
 *
 * @author nicolobartolinii
 */
object UserUtils {
    val auth: FirebaseAuth = Firebase.auth

    fun getCurrentUserUid(): String? {
        return auth.currentUser?.uid
    }
}