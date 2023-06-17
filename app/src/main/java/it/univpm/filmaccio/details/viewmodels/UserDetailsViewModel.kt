package it.univpm.filmaccio.details.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.univpm.filmaccio.data.models.User
import it.univpm.filmaccio.main.utils.FirestoreService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserDetailsViewModel(private val uid: String) : ViewModel() {

    private val _thisUser = MutableStateFlow<User?>(null)
    val thisUser: StateFlow<User?> get() = _thisUser

    init {
        loadThisUser()
    }

    private fun loadThisUser() = viewModelScope.launch {
        val user = FirestoreService.getUserByUid(uid).collect {
            _thisUser.value = it
        }
    }
}