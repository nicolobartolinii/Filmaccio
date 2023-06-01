package it.univpm.filmaccio.main.utils

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import it.univpm.filmaccio.data.models.User
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

object FirestoreService {

    fun getUsers(): Task<QuerySnapshot> {
        return FirebaseFirestore.getInstance().collection("users").get()
    }

    fun getUsersWhereEqualTo(field: String, value: String): Task<QuerySnapshot> {
        return FirebaseFirestore.getInstance().collection("users").whereEqualTo(field, value).get()
    }

    fun getUserByUid(uid: String) = flow {
        val user = FirebaseFirestore.getInstance().collection("users").document(uid).get().await()
            .toObject(User::class.java)
        emit(user)
    }

    fun searchUsers(query: String) = flow {
        val usersCollection = FirebaseFirestore.getInstance().collection("users")
        val snapshot = usersCollection
            .orderBy("username")
            .startAt(query)
            .endAt(query + "\uf8ff")
            .get()
            .await()
        val users = snapshot.toObjects(User::class.java)
        emit(users)
    }
}