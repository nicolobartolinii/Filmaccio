package it.univpm.filmaccio.main.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import it.univpm.filmaccio.data.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

object FirestoreService {
    val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    fun getUsers(): Task<QuerySnapshot> {
        return FirebaseFirestore.getInstance().collection("users").get()
    }

    fun getUsersWhereEqualTo(field: String, value: String): Task<QuerySnapshot> {
        return FirebaseFirestore.getInstance().collection("users").whereEqualTo(field, value).get()
    }

    fun getUserByUid(uid: String) = flow {
        val user = FirebaseFirestore.getInstance().collection("users").document(uid).get().await().toObject(User::class.java)
        emit(user)
    }

    fun searchUsers(query: String) = flow {
        val users = FirebaseFirestore.getInstance().collection("users").whereGreaterThanOrEqualTo("username", query).get().await().toObjects(User::class.java)
        emit(users)
    }
}