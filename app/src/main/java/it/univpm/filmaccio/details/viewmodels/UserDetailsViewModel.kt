package it.univpm.filmaccio.details.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import it.univpm.filmaccio.data.models.User
import it.univpm.filmaccio.data.repository.MovieRepository
import it.univpm.filmaccio.data.repository.UsersRepository
import it.univpm.filmaccio.main.utils.FirestoreService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import androidx.lifecycle.MutableLiveData
class UserDetailsViewModel(private val uid: String) : ViewModel() {


    private val usersRepository = UsersRepository()
    private val _isUserFollowed = MutableLiveData <Boolean?>(null)
    val isUserFollowed: LiveData<Boolean?> get() = _isUserFollowed

    init {
        viewModelScope.launch {
            _isUserFollowed.value = usersRepository.isUserFollowed(uid)
        }
    }

}

class UserDetailsViewModelFactory(private val uid: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserDetailsViewModel(uid) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}