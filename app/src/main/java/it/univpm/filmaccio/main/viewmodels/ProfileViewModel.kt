package it.univpm.filmaccio.main.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.univpm.filmaccio.data.models.User
import it.univpm.filmaccio.main.utils.FirestoreService
import it.univpm.filmaccio.main.utils.UserUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@Suppress("UNCHECKED_CAST")
class ProfileViewModel : ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> get() = _currentUser

    private val _lists = MutableStateFlow<Map<String, List<Long>>?>(emptyMap())
    val lists: StateFlow<Map<String, List<Long>>?> get() = _lists

    init {
        loadCurrentUser()
        getLists()
    }

    private fun loadCurrentUser() = viewModelScope.launch {
        val currentUserUid = UserUtils.getCurrentUserUid()
        FirestoreService.getUserByUid(currentUserUid!!).collect {
            _currentUser.value = it
        }
    }

    private fun getLists() = viewModelScope.launch {
        val currentUserUid = UserUtils.getCurrentUserUid()
        FirestoreService.getLists(currentUserUid!!).collect {
            _lists.value = it as Map<String, List<Long>>?
        }
    }

}