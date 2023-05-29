package it.univpm.filmaccio.main.utils

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object FirestoreService {
    private val db: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    fun getDbAAA(): FirebaseFirestore {
        return db
    }

    fun getUsers(): Task<QuerySnapshot> {
        return this.db.collection("users").get()
    }
}