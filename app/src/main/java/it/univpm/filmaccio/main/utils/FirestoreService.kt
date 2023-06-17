@file:Suppress("UNCHECKED_CAST")

package it.univpm.filmaccio.main.utils

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import it.univpm.filmaccio.data.models.User
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

object FirestoreService {

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

    fun getFollowers(uid: String) = flow {
        val doc = FirebaseFirestore.getInstance().collection("follow").document(uid).get().await()
        val followers = doc.get("followers") as List<String>
        emit(followers)
    }

    fun getFollowing(uid: String) = flow {
        val doc = FirebaseFirestore.getInstance().collection("follow").document(uid).get().await()
        val following = doc.get("following") as List<String>
        emit(following)
    }

    fun followUser(uid: String, targetUid: String) {
        val followRef = FirebaseFirestore.getInstance().collection("follow").document(uid)
        followRef.update("following", FieldValue.arrayUnion(targetUid))

        val followerRef = FirebaseFirestore.getInstance().collection("follow").document(targetUid)
        followerRef.update("followers", FieldValue.arrayUnion(uid))
    }

    fun unfollowUser(uid: String, targetUid: String) {
        val followRef = FirebaseFirestore.getInstance().collection("follow").document(uid)
        followRef.update("following", FieldValue.arrayRemove(targetUid))

        val followerRef = FirebaseFirestore.getInstance().collection("follow").document(targetUid)
        followerRef.update("followers", FieldValue.arrayRemove(uid))
    }

    fun addToList(uid: String, listName: String, itemId: Long) {
        val listsRef = FirebaseFirestore.getInstance().collection("lists").document(uid)
        listsRef.update(listName, FieldValue.arrayUnion(itemId))
    }

    fun removeFromList(uid: String, listName: String, itemId: Long) {
        val listsRef = FirebaseFirestore.getInstance().collection("lists").document(uid)
        listsRef.update(listName, FieldValue.arrayRemove(itemId))
    }

    fun getList(uid: String, listName: String) = flow {
        val doc = FirebaseFirestore.getInstance().collection("lists").document(uid).get().await()
        val list = doc.get(listName) as List<Int>
        emit(list)
    }

    fun getLists(uid: String) = flow {
        val doc = FirebaseFirestore.getInstance().collection("lists").document(uid).get().await()
        val lists = doc.data as Map<String, List<Int>>
        emit(lists)
    }
}