package it.univpm.filmaccio.main.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.univpm.filmaccio.data.models.User
import it.univpm.filmaccio.main.utils.FirestoreService
import it.univpm.filmaccio.main.utils.UserUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> get() = _currentUser

    private val _lists = MutableStateFlow<Map<String, Any>?>(emptyMap())
    val lists: StateFlow<Map<String, Any>?> get() = _lists

    init {
        loadCurrentUser()
    }

    private fun loadCurrentUser() = viewModelScope.launch {
        val currentUserUid = UserUtils.getCurrentUserUid()
        val user = FirestoreService.getUserByUid(currentUserUid!!).collect {
            _currentUser.value = it
        }
    }

    private fun getLists() = viewModelScope.launch {
        val currentUserUid = UserUtils.getCurrentUserUid()
        val user = FirestoreService.getLists(currentUserUid!!).collect {
            _lists.value = it
        }
    }

}