package it.univpm.filmaccio.details.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import it.univpm.filmaccio.data.models.User
import it.univpm.filmaccio.data.repository.UsersRepository
import it.univpm.filmaccio.main.utils.FirestoreService
import it.univpm.filmaccio.main.utils.UserUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class UserDetailsViewModel(private val uid: String) : ViewModel() {

    private val usersRepository = UsersRepository()

    // Qui creiamo un oggetto MutableStateFlow che conterrà l'utente corrente.[ devo cambiarlo nell'user che gli passo]
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> get() = _currentUser

    private val _isUserFollowed = MutableLiveData<Boolean?>(null)
    val isUserFollowed: LiveData<Boolean?> get() = _isUserFollowed

    // Qui creiamo un oggetto MutableStateFlow che conterrà le liste dell'utente corrente.
    private val _lists = MutableStateFlow<Map<String, List<Long>>?>(emptyMap())
    val lists: StateFlow<Map<String, List<Long>>?> get() = _lists

    var isFirstLaunch: Boolean = true

    init {
        loadCurrentUser()
        viewModelScope.launch {
            _isUserFollowed.value = usersRepository.isUserFollowed(uid)
        }
        getLists()
    }

    private fun getLists() = viewModelScope.launch {
        FirestoreService.getLists(uid).collect {
            _lists.value = it
        }
    }

    private fun loadCurrentUser() = viewModelScope.launch {
        FirestoreService.getUserByUid(uid).collect {
            _currentUser.value = it
        }
    }

    class UserDetailsViewModelFactory(private val uid: String) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(UserDetailsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST") return UserDetailsViewModel(uid) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}