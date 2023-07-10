package it.univpm.filmaccio.data.repository

import it.univpm.filmaccio.main.utils.FirestoreService
import it.univpm.filmaccio.main.utils.UserUtils
import kotlinx.coroutines.flow.first

/**
 * Questa classe è un repository che si occupa di gestire i dati relativi agli utenti.
 *
 * @author NicolaPiccia
 * @author nicolobartolinii
 */
class UsersRepository {

    // Questo metodo si aggancia all'oggetto FirestoreService per controllare se l'utente è già seguito dall'utente corrente
    suspend fun isUserFollowed(userId: String): Boolean {
        val followers: List<String> = FirestoreService.getFollowers(userId).first()
        return UserUtils.getCurrentUserUid() in followers
    }

    // Questo metodo si aggancia all'oggetto FirestoreService per aggiornare i dati di un utente
    fun updateUserField(userId: String, field: String, value: Any, callback: (Boolean) -> Unit) {
        FirestoreService.updateUserField(userId, field, value, callback)
    }

    // Questo metodo si aggancia all'oggetto FirestoreService per ottenere i dettagli di un utente
    suspend fun getUserDetails(userId: String): it.univpm.filmaccio.data.models.User {
        return FirestoreService.getUserByUid(userId).first()!!
    }
}