package it.univpm.filmaccio.main.utils

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import it.univpm.filmaccio.data.models.User

object FirestoreService {

    fun getUsers(): Task<QuerySnapshot> {
        return FirebaseFirestore.getInstance().collection("users").get()
    }

    fun getUsersWhereEqualTo(field: String, value: String): Task<QuerySnapshot> {
        return FirebaseFirestore.getInstance().collection("users").whereEqualTo(field, value).get()
    }

    fun getUserByUid(uid: String): User? {
        var user: User? = null
        FirebaseFirestore.getInstance().collection("users").document(uid).get().addOnSuccessListener { document ->
                user = document.toObject(User::class.java)
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()}
        return user
    }
}