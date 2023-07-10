@file:Suppress("UNCHECKED_CAST")

package it.univpm.filmaccio.main.utils

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import it.univpm.filmaccio.data.models.ReviewTriple
import it.univpm.filmaccio.data.models.User
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Questo oggetto singleton si occupa di gestire tutte le operazioni che riguardano il database
 * Firestore. In particolare, contiene un oggetto FirebaseFirestore che rappresenta il database
 * e contiene i riferimenti alle collezioni del database.
 * Poi ci sono parecchi metodi che permettono di effettuare operazioni sul database. Sono abbastanza
 * autoesplicativi.
 *
 * @author nicolobartolinii
 * @author NicolaPiccia
 */
object FirestoreService {

    private val db: FirebaseFirestore by lazy { Firebase.firestore }
    val collectionUsers = db.collection("users")
    val collectionFollow = db.collection("follow")
    val collectionLists = db.collection("lists")
    val collectionEpisodes = db.collection("episodes")
    val collectionUsersReviews = db.collection("usersReviews")
    private val collectionProductsReviews = db.collection("productsReviews")

    fun getUserByUid(uid: String) = flow {
        val user = collectionUsers.document(uid).get().await().toObject(User::class.java)
        emit(user)
    }

    fun searchUsers(query: String) = flow {
        val snapshot =
            collectionUsers.orderBy("username").startAt(query).endAt(query + "\uf8ff").get().await()
        val users = snapshot.toObjects(User::class.java)
        emit(users)
    }

    fun getWhereEqualTo(collection: String, field: String, value: String): Query {
        return db.collection(collection).whereEqualTo(field, value)
    }

    fun updateUserField(uid: String, field: String, value: Any, callback: (Boolean) -> Unit) {
        val userRef = collectionUsers.document(uid)
        userRef.update(field, value).addOnCompleteListener {
            callback(it.isSuccessful)
        }.addOnFailureListener {
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
            "watchingSeries.$seriesId.$seasonNumber", FieldValue.arrayUnion(episodeNumber)
        )
    }

    fun removeEpisodeFromWatched(
        uid: String, seriesId: Long, seasonNumber: Long, episodeNumber: Long
    ) {
        val docRef = collectionEpisodes.document(uid)
        docRef.update(
            "watchingSeries.$seriesId.$seasonNumber", FieldValue.arrayRemove(episodeNumber)
        )
    }

    fun checkIfEpisodeWatched(
        uid: String, seriesId: Long, seasonNumber: Long, episodeNumber: Long
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

    suspend fun getMovieRating(uid: String, movieId: Long): Pair<Float, Timestamp> {
        val doc = collectionUsersReviews.document(uid).get().await()
        val ratingValueDoc = doc.get("movies.$movieId.rating.value") as Double?
        val ratingValue = ratingValueDoc?.toFloat() ?: 0f
        val ratingTimestampDoc = doc.get("movies.$movieId.rating.timestamp") as Timestamp?
        val ratingTimestamp = ratingTimestampDoc ?: Timestamp.now()
        return Pair(ratingValue, ratingTimestamp)
    }

    suspend fun getMovieReview(uid: String, movieId: Long): Pair<String, Timestamp> {
        val doc = collectionUsersReviews.document(uid).get().await()
        val reviewDoc = doc.get("movies.$movieId.review.text") as String?
        val review = reviewDoc ?: ""
        val reviewTimestampDoc = doc.get("movies.$movieId.review.timestamp") as Timestamp?
        val reviewTimestamp = reviewTimestampDoc ?: Timestamp.now()
        return Pair(review, reviewTimestamp)
    }

    suspend fun updateMovieRating(uid: String, movieId: Long, rating: Float, timestamp: Timestamp) {
        val docRef = collectionUsersReviews.document(uid)
        val productsDocRef = collectionProductsReviews.document("movies")
        val currentRating = getMovieRating(uid, movieId).first
        if (currentRating != 0f) {
            docRef.update("movies.$movieId.rating.value", rating)
            docRef.update("movies.$movieId.rating.timestamp", timestamp)
            productsDocRef.update(
                "$movieId.value", FieldValue.increment(rating.toDouble() - currentRating.toDouble())
            )
        } else {
            docRef.update(
                "movies.$movieId.rating", mapOf("value" to rating, "timestamp" to timestamp)
            )
            productsDocRef.update("$movieId.ratings", FieldValue.arrayUnion(uid))
            productsDocRef.update("$movieId.value", FieldValue.increment(rating.toDouble()))
        }
    }

    suspend fun updateMovieReview(
        uid: String, movieId: Long, review: String, timestamp: Timestamp
    ) {
        val docRef = collectionUsersReviews.document(uid)
        val productsDocRef = collectionProductsReviews.document("movies")
        val currentReview = getMovieReview(uid, movieId).first
        if (currentReview != "") {
            docRef.update("movies.$movieId.review.text", review)
            docRef.update("movies.$movieId.review.timestamp", timestamp)
        } else {
            docRef.update(
                "movies.$movieId.review", mapOf("text" to review, "timestamp" to timestamp)
            )
            productsDocRef.update("$movieId.reviews", FieldValue.arrayUnion(uid))
        }
    }

    suspend fun getSeriesRating(uid: String, seriesId: Long): Pair<Float, Timestamp> {
        val doc = collectionUsersReviews.document(uid).get().await()
        val ratingValueDoc = doc.get("series.$seriesId.rating.value") as Double?
        val ratingValue = ratingValueDoc?.toFloat() ?: 0f
        val ratingTimestampDoc = doc.get("series.$seriesId.rating.timestamp") as Timestamp?
        val ratingTimestamp = ratingTimestampDoc ?: Timestamp.now()
        return Pair(ratingValue, ratingTimestamp)
    }

    suspend fun getSeriesReview(uid: String, seriesId: Long): Pair<String, Timestamp> {
        val doc = collectionUsersReviews.document(uid).get().await()
        val reviewDoc = doc.get("series.$seriesId.review.text") as String?
        val review = reviewDoc ?: ""
        val reviewTimestampDoc = doc.get("series.$seriesId.review.timestamp") as Timestamp?
        val reviewTimestamp = reviewTimestampDoc ?: Timestamp.now()
        return Pair(review, reviewTimestamp)
    }

    suspend fun updateSeriesRating(
        uid: String, seriesId: Long, rating: Float, timestamp: Timestamp
    ) {
        val docRef = collectionUsersReviews.document(uid)
        val productsDocRef = collectionProductsReviews.document("series")
        val currentRating = getSeriesRating(uid, seriesId).first
        if (currentRating != 0f) {
            docRef.update("series.$seriesId.rating.value", rating)
            docRef.update("series.$seriesId.rating.timestamp", timestamp)
            productsDocRef.update(
                "$seriesId.value",
                FieldValue.increment(rating.toDouble() - currentRating.toDouble())
            )
        } else {
            docRef.update(
                "series.$seriesId.rating", mapOf("value" to rating, "timestamp" to timestamp)
            )
            productsDocRef.update("$seriesId.ratings", FieldValue.arrayUnion(uid))
            productsDocRef.update("$seriesId.value", FieldValue.increment(rating.toDouble()))
        }
    }

    suspend fun updateSeriesReview(
        uid: String, seriesId: Long, review: String, timestamp: Timestamp
    ) {
        val docRef = collectionUsersReviews.document(uid)
        val productsDocRef = collectionProductsReviews.document("series")
        val currentReview = getSeriesReview(uid, seriesId).first
        if (currentReview != "") {
            docRef.update("series.$seriesId.review.text", review)
            docRef.update("series.$seriesId.review.timestamp", timestamp)
        } else {
            docRef.update(
                "series.$seriesId.review", mapOf("text" to review, "timestamp" to timestamp)
            )
            productsDocRef.update("$seriesId.reviews", FieldValue.arrayUnion(uid))
        }
    }

    suspend fun getAverageMovieRating(movieId: Long): Float {
        val docRef = collectionProductsReviews.document("movies")
        var value = 0f
        var users = listOf<String>()
        docRef.get().addOnSuccessListener {
            users =
                if (it.get("$movieId.ratings") != null) it.get("$movieId.ratings") as List<String>
                else listOf()
            value =
                if ((it.get("$movieId.value") as Double?) != null) (it.get("$movieId.value") as Double).toFloat()
                else 0f
        }.await()
        return if (users.isEmpty()) 0f
        else value / users.size
    }

    suspend fun getAverageSeriesRating(seriesId: Long): Float {
        val docRef = collectionProductsReviews.document("series")
        var value = 0f
        var users = listOf<String>()
        docRef.get().addOnSuccessListener {
            users =
                if (it.get("$seriesId.ratings") != null) it.get("$seriesId.ratings") as List<String>
                else listOf()
            value =
                if ((it.get("$seriesId.value") as Double?) != null) (it.get("$seriesId.value") as Double).toFloat()
                else 0f
        }.await()
        return if (users.isEmpty()) 0f
        else value / users.size
    }

    suspend fun getMovieReviews(movieId: Long): List<ReviewTriple> {
        val docRef = collectionProductsReviews.document("movies")
        var users = listOf<String>()
        docRef.get().addOnSuccessListener {
            users =
                if (it.get("$movieId.reviews") != null) it.get("$movieId.reviews") as List<String>
                else listOf()
        }.await()
        val reviews = mutableListOf<ReviewTriple>()
        for (user in users) {
            val userData = getUserByUid(user).first()!!
            userData.birthDate
            val reviewData = getMovieReview(user, movieId)
            val date = reviewData.second.toDate()
            val format = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ITALY)
            val dateString = format.format(date)
            val review = ReviewTriple(userData, reviewData.first, dateString)
            reviews.add(review)
        }
        return reviews
    }

    suspend fun getAllRatings(type: String): List<Pair<Long, Double>> {
        val docRef = collectionProductsReviews.document(type).get().await()
        val document = docRef.data as MutableMap<String, Map<String, Any>>
        val ratings = mutableListOf<Pair<Long, Double>>()
        for (movie in document) {
            val movieId = movie.key.toLong()
            if (movie.value["ratings"] == null) continue
            val rating =
                (movie.value["value"] as Double) / (movie.value["ratings"] as List<String>).size
            val pair = Pair(movieId, rating)
            ratings.add(pair)
        }
        return ratings
    }

    suspend fun getSeriesReviews(seriesId: Long): List<ReviewTriple> {
        val docRef = collectionProductsReviews.document("series")
        var users = listOf<String>()
        docRef.get().addOnSuccessListener {
            users =
                if (it.get("$seriesId.reviews") != null) it.get("$seriesId.reviews") as List<String>
                else listOf()
        }.await()
        val reviews = mutableListOf<ReviewTriple>()
        for (user in users) {
            val userData = getUserByUid(user).first()!!
            userData.birthDate
            val reviewData = getSeriesReview(user, seriesId)
            val date = reviewData.second.toDate()
            val format = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ITALY)
            val dateString = format.format(date)
            val review = ReviewTriple(userData, reviewData.first, dateString)
            reviews.add(review)
        }
        return reviews
    }

    suspend fun getUserReviews(userId: String, type: String): Pair<ReviewTriple, Long>? {
        val docRef = collectionUsersReviews.document(userId).get().await()
        val reviews = docRef.get(type) as Map<String, Map<String, Map<String, Any>>>?
        val latestReview = if (reviews != null) findLatestReview(reviews)
        else return null
        val reviewTriple = ReviewTriple(
            getUserByUid(userId).first()!!,
            latestReview?.second ?: "",
            latestReview?.third ?: "01/01/1970 00:00"
        )
        return Pair(reviewTriple, latestReview?.first?.toLong() ?: 0)
    }

    private fun findLatestReview(map: Map<String, Map<String, Map<String, Any>>>): Triple<String, String, String>? {
        var latestReview: Triple<String, String, String>? = null
        var latestTimestamp: Timestamp? = null

        for ((key1, value1) in map) {
            val value2 = value1["review"] ?: continue
            val timestamp = value2["timestamp"] as Timestamp
            if (latestTimestamp == null || timestamp > latestTimestamp) {
                latestTimestamp = timestamp
                val date = latestTimestamp.toDate()
                val format = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ITALY)
                val dateString = format.format(date)
                latestReview = Triple(key1, value2["text"] as String, dateString)
            }
        }
        return latestReview
    }
}