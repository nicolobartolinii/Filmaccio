package it.univpm.filmaccio.details.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import it.univpm.filmaccio.data.repository.PeopleRepository
import it.univpm.filmaccio.main.utils.UserUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * ViewModel per la gestione dei dettagli di una persona (attore, regista, ecc.)
 *
 * @param personId id della persona
 *
 * @author nicolobartolinii
 */
class PersonDetailsViewModel(private var personId: Long = 0L) : ViewModel() {
    private val peopleRepository = PeopleRepository()

    val currentPerson = liveData(Dispatchers.IO) {
        val person = peopleRepository.getPersonDetails(personId)
        emit(person)
    }

    private val _isFollowed = MutableLiveData<Boolean>()
    val isFollowed: LiveData<Boolean> = _isFollowed

    init {
        viewModelScope.launch {
            _isFollowed.value =
                peopleRepository.isPersonFollowed(UserUtils.getCurrentUserUid()!!, personId)
        }
    }

    fun followPerson() {
        peopleRepository.followPerson(UserUtils.getCurrentUserUid()!!, personId)
        _isFollowed.value = true
    }

    fun unfollowPerson() {
        peopleRepository.unfollowPerson(UserUtils.getCurrentUserUid()!!, personId)
        _isFollowed.value = false
    }

    /**
     * Factory per la creazione di un PersonDetailsViewModel con parametri personalizzati
     *
     * @param personId id della persona
     *
     * @author nicolobartolinii
     */
    class PersonDetailsViewModelFactory(private val personId: Long) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PersonDetailsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST") return PersonDetailsViewModel(personId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}