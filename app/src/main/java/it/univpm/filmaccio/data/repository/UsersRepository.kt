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
}