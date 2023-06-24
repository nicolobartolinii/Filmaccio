package it.univpm.filmaccio.main.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.univpm.filmaccio.data.models.User
import it.univpm.filmaccio.main.utils.FirestoreService
import it.univpm.filmaccio.main.utils.UserUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Questa classe è il ViewModel della schermata del profilo dell'applicazione, quindi si occupa di gestire
// i dati relativi all'utente che vengono mostrati nella schermata del profilo.
class ProfileViewModel : ViewModel() {

    // Qui creiamo un oggetto MutableStateFlow che conterrà l'utente corrente.
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> get() = _currentUser

    // Qui creiamo un oggetto MutableStateFlow che conterrà le liste dell'utente corrente.
    private val _lists = MutableStateFlow<Map<String, List<Long>>?>(emptyMap())
    val lists: StateFlow<Map<String, List<Long>>?> get() = _lists

    // Quando il ViewModel viene iniziato, carichiamo l'utente corrente e le sue liste.
    init {
        loadCurrentUser()
        getLists()
    }

    // Questo metodo si occupa di caricare l'utente corrente. Per farlo, utilizza il metodo
    // getUserByUid del FirestoreService, che restituisce un Flow<User?>. Questo Flow viene
    // osservato e ogni volta che cambia il valore, viene emesso un nuovo valore per l'oggetto
    // _currentUser. Ovviamente cambia solo quando il ViewModel viene inizializzato, perché
    // inizialmente currentUser è null e poi non cambia più (ovviamente a meno che l'utente non
    // effettui il Logout, ma in quel caso verrebbe ricaricato comunque l'intero fragment).
    private fun loadCurrentUser() = viewModelScope.launch {
        val currentUserUid = UserUtils.getCurrentUserUid()
        FirestoreService.getUserByUid(currentUserUid!!).collect {
            _currentUser.value = it
        }
    }

    // Questo metodo si occupa di caricare le liste dell'utente corrente. Sostanzialmente
    // funziona allo stesso modo di loadCurrentUser, ma in questo caso viene utilizzato il
    // metodo getLists del FirestoreService, che restituisce un Flow<Map<String, List<Long>>?>.
    private fun getLists() = viewModelScope.launch {
        val currentUserUid = UserUtils.getCurrentUserUid()
        FirestoreService.getLists(currentUserUid!!).collect {
            _lists.value = it as Map<String, List<Long>>?
        }
    }

}