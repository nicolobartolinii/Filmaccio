package it.univpm.filmaccio.data.repository

import it.univpm.filmaccio.main.utils.FirestoreService
import it.univpm.filmaccio.main.utils.UserUtils
import kotlinx.coroutines.flow.first

class UsersRepository {
    // restituisce un flow asincrono di oggetti, mi restituisce il primo
    suspend fun isUserFollowed(userId: String): Boolean {
        val followers: List<String> = FirestoreService.getFollowers(userId).first()
        return UserUtils.getCurrentUserUid() in followers
    }

    fun updateUserField(userId: String, field: String, value: Any, callback: (Boolean) -> Unit) {
        FirestoreService.updateUserField(userId, field, value, callback)
    }

    suspend fun getUserDetails(userId: String): it.univpm.filmaccio.data.models.User {
        return FirestoreService.getUserByUid(userId).first()!!
    }
}