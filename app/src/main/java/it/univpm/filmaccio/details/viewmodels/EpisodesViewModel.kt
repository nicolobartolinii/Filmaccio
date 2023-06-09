@file:Suppress("unused")

package it.univpm.filmaccio.details.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import it.univpm.filmaccio.main.utils.FirestoreService
import it.univpm.filmaccio.main.utils.UserUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel per la gestione degli episodi di una serie
 *
 * @param seriesId id della serie
 * @param seasonNumber numero della stagione
 * @param episodeNumber numero dell'episodio
 *
 * @author nicolobartolinii
 */
class EpisodesViewModel(
    private val seriesId: Long, private val seasonNumber: Long, private val episodeNumber: Long
) : ViewModel() {

    private val uid = UserUtils.getCurrentUserUid()!!

    private val _isEpisodeWatched = MutableStateFlow(false)
    val isEpisodeWatched: StateFlow<Boolean> = _isEpisodeWatched

    init {
        viewModelScope.launch {
            FirestoreService.checkIfEpisodeWatched(uid, seriesId, seasonNumber, episodeNumber)
                .collect {
                    _isEpisodeWatched.value = it
                }
        }
    }
}

/**
 * Factory per la creazione di un EpisodesViewModel con parametri personalizzati
 *
 * @param seriesId id della serie
 * @param seasonNumber numero della stagione
 * @param episodeNumber numero dell'episodio
 *
 * @author nicolobartolinii
 */
class EpisodesViewModelFactory(
    private val seriesId: Long, private val seasonNumber: Long, private val episodeNumber: Long
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EpisodesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return EpisodesViewModel(
                seriesId,
                seasonNumber,
                episodeNumber
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}