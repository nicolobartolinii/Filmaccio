package it.univpm.filmaccio.details.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import it.univpm.filmaccio.data.repository.PeopleRepository
import kotlinx.coroutines.Dispatchers

class PersonDetailsViewModel(private var personId: Long = 0L) : ViewModel() {
    private val peopleRepository = PeopleRepository()

    val currentPerson = liveData(Dispatchers.IO) {
        val person = peopleRepository.getPersonDetails(personId)
        emit(person)
    }
}

class PersonDetailsViewModelFactory(private val personId: Long) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PersonDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PersonDetailsViewModel(personId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}