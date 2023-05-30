package it.univpm.filmaccio.main.viewmodels

import androidx.lifecycle.ViewModel
import it.univpm.filmaccio.data.models.User
import it.univpm.filmaccio.main.utils.FirestoreService
import it.univpm.filmaccio.main.utils.UserUtils

class ProfileViewModel : ViewModel() {

    private val currentUserUid = UserUtils.getCurrentUserUid()

    val currentUser = FirestoreService.getUserByUid(currentUserUid ?: "")

}