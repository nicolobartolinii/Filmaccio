package it.univpm.filmaccio.details.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import it.univpm.filmaccio.data.repository.SeriesRepository
import it.univpm.filmaccio.main.utils.UserUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SeriesDetailsViewModel(private var seriesId: Int = 0) : ViewModel() {
    private val seriesRepository = SeriesRepository()

    private val userId = UserUtils.getCurrentUserUid()!!

    private val _isSeriesInWatching = MutableLiveData<Boolean>()
    val isSeriesInWatching: LiveData<Boolean> = _isSeriesInWatching

    private val _isSeriesInWatchlist = MutableLiveData<Boolean>()
    val isSeriesInWatchlist: LiveData<Boolean> = _isSeriesInWatchlist

    private val _isSeriesFavorited = MutableLiveData<Boolean>()
    val isSeriesFavorited: LiveData<Boolean> = _isSeriesFavorited

    init {
        viewModelScope.launch {
            _isSeriesInWatching.value = seriesRepository.isSeriesInWatching(userId, seriesId)
        }
        viewModelScope.launch {
            _isSeriesInWatchlist.value = seriesRepository.isSeriesInWatchlist(userId, seriesId)
        }
        viewModelScope.launch {
            _isSeriesFavorited.value = seriesRepository.isSeriesFavorited(userId, seriesId)
        }
    }

    fun toggleInWatching(seriesId: Int) = viewModelScope.launch {
        if (_isSeriesInWatching.value == true) {
            seriesRepository.removeFromList(userId, "watching_t", seriesId)
            _isSeriesInWatching.value = false
        } else {
            seriesRepository.addToList(userId, "watching_t", seriesId)
            if (_isSeriesInWatchlist.value == true) {
                seriesRepository.removeFromList(userId, "watchlist_t", seriesId)
                _isSeriesInWatchlist.value = false
            }
            _isSeriesInWatching.value = true
        }
    }

    fun toggleWatchlist(seriesId: Int) = viewModelScope.launch {
        if (_isSeriesInWatchlist.value == true) {
            seriesRepository.removeFromList(userId, "watchlist_t", seriesId)
            _isSeriesInWatchlist.value = false
        } else {
            seriesRepository.addToList(userId, "watchlist_t", seriesId)
            _isSeriesInWatchlist.value = true
        }
    }

    fun toggleFavorite(seriesId: Int) = viewModelScope.launch {
        if (_isSeriesFavorited.value == true) {
            seriesRepository.removeFromList(userId, "favorite_t", seriesId)
            _isSeriesFavorited.value = false
        } else {
            seriesRepository.addToList(userId, "favorite_t", seriesId)
            _isSeriesFavorited.value = true
        }
    }

    val currentSeries = liveData(Dispatchers.IO) {
        val series = seriesRepository.getSeriesDetails(seriesId)
        emit(series)
    }
}

class SeriesDetailsViewModelFactory(private val seriesId: Int) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SeriesDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SeriesDetailsViewModel(seriesId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}