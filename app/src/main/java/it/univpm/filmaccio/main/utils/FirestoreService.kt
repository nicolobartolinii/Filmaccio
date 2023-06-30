@file:Suppress("UNCHECKED_CAST")

package it.univpm.filmaccio.main.utils

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import it.univpm.filmaccio.data.models.User
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

// Questo oggetto singleton si occupa di gestire tutte le operazioni che riguardano il database
// Firestore. In particolare, contiene un oggetto FirebaseFirestore che rappresenta il database
// e contiene i riferimenti alle collezioni del database.
// Poi ci sono parecchi metodi che permettono di effettuare operazioni sul database. Sono abbastanza
// autoesplicativi, quindi non mi metterò a commentarli.
object FirestoreService {

    val db: FirebaseFirestore by lazy { Firebase.firestore }
    val collectionUsers = db.collection("users")
    val collectionFollow = db.collection("follow")
    val collectionLists = db.collection("lists")
    val collectionEpisodes = db.collection("episodes")

    fun getUserByUid(uid: String) = flow {
        val user = collectionUsers.document(uid).get().await()
            .toObject(User::class.java)
        emit(user)
    }

    fun searchUsers(query: String) = flow {
        val snapshot = collectionUsers
            .orderBy("username")
            .startAt(query)
            .endAt(query + "\uf8ff")
            .get()
            .await()
        val users = snapshot.toObjects(User::class.java)
        emit(users)
    }

    fun getWhereEqualTo(collection: String, field: String, value: String): Query {
        return db.collection(collection).whereEqualTo(field, value)
    }

    fun updateUserField(uid: String, field: String, value: Any, callback: (Boolean) -> Unit) {
        val userRef = collectionUsers.document(uid)
        userRef.update(field, value)
            .addOnCompleteListener {
                callback(it.isSuccessful)
            }
            .addOnFailureListener {
                callback(false)
            }
    }


    fun getFollowers(uid: String) = flow {
        val doc = collectionFollow.document(uid).get().await()
        val followers = doc.get("followers") as List<String>
        emit(followers)
    }

    fun getFollowing(uid: String) = flow {
        val doc = collectionFollow.document(uid).get().await()
        val following = doc.get("following") as List<String>
        emit(following)
    }

    fun getPeopleFollowed(uid: String) = flow {
        val doc = collectionFollow.document(uid).get().await()
        val people = doc.get("people") as List<Long>
        emit(people)
    }

    fun followUser(uid: String, targetUid: String) {
        val followRef = collectionFollow.document(uid)
        followRef.update("following", FieldValue.arrayUnion(targetUid))

        val followerRef = collectionFollow.document(targetUid)
        followerRef.update("followers", FieldValue.arrayUnion(uid))
    }

    fun unfollowUser(uid: String, targetUid: String) {
        val followRef = collectionFollow.document(uid)
        followRef.update("following", FieldValue.arrayRemove(targetUid))

        val followerRef = collectionFollow.document(targetUid)
        followerRef.update("followers", FieldValue.arrayRemove(uid))
    }

    fun followPerson(uid: String, personId: Long) {
        val followRef = collectionFollow.document(uid)
        followRef.update("people", FieldValue.arrayUnion(personId))
    }

    fun unfollowPerson(uid: String, personId: Long) {
        val followRef = collectionFollow.document(uid)
        followRef.update("people", FieldValue.arrayRemove(personId))
    }

    fun addToList(uid: String, listName: String, itemId: Long) {
        val listsRef = collectionLists.document(uid)
        listsRef.update(listName, FieldValue.arrayUnion(itemId))
    }

    fun removeFromList(uid: String, listName: String, itemId: Long) {
        val listsRef = collectionLists.document(uid)
        listsRef.update(listName, FieldValue.arrayRemove(itemId))
    }

    fun getList(uid: String, listName: String) = flow {
        val doc = collectionLists.document(uid).get().await()
        val list = doc.get(listName) as List<Long>
        emit(list)
    }

    fun getLists(uid: String) = flow {
        val doc = collectionLists.document(uid).get().await()
        val lists = doc.data as Map<String, List<Long>>
        emit(lists)
    }

    fun addSeriesToWatching(uid: String, seriesId: Long) {
        val docRef = collectionEpisodes.document(uid)

        val initialSeriesData = hashMapOf<String, Map<String, List<Long>>>()
        docRef.update("watchingSeries.$seriesId", initialSeriesData)
    }

    fun addWatchedEpisode(uid: String, seriesId: Long, seasonNumber: Long, episodeNumber: Long) {
        val docRef = collectionEpisodes.document(uid)
        docRef.update(
            "watchingSeries.$seriesId.$seasonNumber",
            FieldValue.arrayUnion(episodeNumber)
        )
    }

    fun removeEpisodeFromWatched(
        uid: String,
        seriesId: Long,
        seasonNumber: Long,
        episodeNumber: Long
    ) {
        val docRef = collectionEpisodes.document(uid)
        docRef.update(
            "watchingSeries.$seriesId.$seasonNumber",
            FieldValue.arrayRemove(episodeNumber)
        )
    }

    fun checkIfEpisodeWatched(
        uid: String,
        seriesId: Long,
        seasonNumber: Long,
        episodeNumber: Long
    ) = flow {
        val doc = collectionEpisodes.document(uid).get().await()
        val series = doc.get("watchingSeries.$seriesId") as Map<String, List<Long>>? ?: mapOf()
        val season = series[seasonNumber.toString()]
        val isWatched = season?.contains(episodeNumber) ?: false
        emit(isWatched)
    }

    fun getWatchingSeries(uid: String) = flow {
        val doc = collectionEpisodes.document(uid).get().await()
        val watchingSeries = doc.get("watchingSeries") as Map<String, Map<String, List<Long>>>
        emit(watchingSeries)
    }

    fun addSeasonToWatchingSeries(uid: String, seriesId: Long, seasonNumber: Long) {
        val docRef = collectionEpisodes.document(uid)

        val initialSeasonData = listOf<Long>()
        docRef.update("watchingSeries.$seriesId.$seasonNumber", initialSeasonData)
    }
}