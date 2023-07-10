package it.univpm.filmaccio.details.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import it.univpm.filmaccio.data.models.ReviewTriple
import it.univpm.filmaccio.data.repository.SeriesRepository
import it.univpm.filmaccio.main.utils.FirestoreService
import it.univpm.filmaccio.main.utils.UserUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * ViewModel per la gestione dei dettagli di una serie tv
 *
 * @param seriesId id della serie tv
 *
 * @author nicolobartolinii
 */
class SeriesDetailsViewModel(private var seriesId: Long = 0L) : ViewModel() {
    private val seriesRepository = SeriesRepository()

    private val userId = UserUtils.getCurrentUserUid()!!

    private val _isSeriesInWatching = MutableLiveData<Boolean>()
    val isSeriesInWatching: LiveData<Boolean> = _isSeriesInWatching

    private val _isSeriesInWatchlist = MutableLiveData<Boolean>()
    val isSeriesInWatchlist: LiveData<Boolean> = _isSeriesInWatchlist

    private val _isSeriesFavorited = MutableLiveData<Boolean>()
    val isSeriesFavorited: LiveData<Boolean> = _isSeriesFavorited

    private val _isSeriesFinished = MutableLiveData<Boolean>()
    val isSeriesFinished: LiveData<Boolean> = _isSeriesFinished

    private val _isSeriesRated = MutableLiveData<Boolean>()
    val isSeriesRated: LiveData<Boolean> = _isSeriesRated

    private val _isSeriesReviewed = MutableLiveData<Boolean>()
    val isSeriesReviewed: LiveData<Boolean> = _isSeriesReviewed

    private val _currentSeriesRating = MutableLiveData<Pair<Float, Timestamp>?>(null)
    val currentSeriesRating: LiveData<Pair<Float, Timestamp>?> = _currentSeriesRating

    private val _currentSeriesReview = MutableLiveData<Pair<String, Timestamp>?>(null)
    val currentSeriesReview: LiveData<Pair<String, Timestamp>?> = _currentSeriesReview

    private val _averageSeriesRating = MutableLiveData<Float>()
    val averageSeriesRating: LiveData<Float> = _averageSeriesRating

    private val _seriesReviews = MutableLiveData<List<ReviewTriple>>(emptyList())
    val seriesReviews: LiveData<List<ReviewTriple>> = _seriesReviews

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
        viewModelScope.launch {
            _isSeriesFinished.value = seriesRepository.isSeriesFinished(userId, seriesId)
        }
        viewModelScope.launch {
            _isSeriesRated.value = seriesRepository.isSeriesRated(userId, seriesId)
        }
        viewModelScope.launch {
            _isSeriesReviewed.value = seriesRepository.isSeriesReviewed(userId, seriesId)
        }
        viewModelScope.launch {
            _averageSeriesRating.value = seriesRepository.getAverageSeriesRating(seriesId)
        }
        viewModelScope.launch {
            _seriesReviews.value = seriesRepository.getSeriesReviews(seriesId)
        }
    }

    fun toggleInWatching(seriesId: Long) = viewModelScope.launch {
        if (_isSeriesInWatching.value == true) {
            seriesRepository.removeFromList(userId, "watching_t", seriesId)
            _isSeriesInWatching.value = false
        } else {
            seriesRepository.addToList(userId, "watching_t", seriesId)
            if (!FirestoreService.getWatchingSeries(userId).first()
                    .containsKey(seriesId.toString())
            ) FirestoreService.addSeriesToWatching(UserUtils.getCurrentUserUid()!!, seriesId)
            if (_isSeriesInWatchlist.value == true) {
                seriesRepository.removeFromList(userId, "watchlist_t", seriesId)
                _isSeriesInWatchlist.value = false
            }
            _isSeriesInWatching.value = true
        }
    }

    fun toggleWatchlist(seriesId: Long) = viewModelScope.launch {
        if (_isSeriesInWatchlist.value == true) {
            seriesRepository.removeFromList(userId, "watchlist_t", seriesId)
            _isSeriesInWatchlist.value = false
        } else {
            seriesRepository.addToList(userId, "watchlist_t", seriesId)
            _isSeriesInWatchlist.value = true
        }
    }

    fun toggleFavorite(seriesId: Long) = viewModelScope.launch {
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

    fun loadCurrentSeriesRating(userId: String, seriesId: Long) {
        viewModelScope.launch {
            _currentSeriesRating.value = seriesRepository.getSeriesRating(userId, seriesId)
        }
    }

    fun loadCurrentSeriesReview(userId: String, seriesId: Long) {
        viewModelScope.launch {
            _currentSeriesReview.value = seriesRepository.getSeriesReview(userId, seriesId)
        }
    }

    fun updateSeriesRating(userId: String, seriesId: Long, rating: Float, timestamp: Timestamp) {
        viewModelScope.launch {
            seriesRepository.updateSeriesRating(userId, seriesId, rating, timestamp)
        }
    }

    fun updateSeriesReview(userId: String, seriesId: Long, review: String, timestamp: Timestamp) {
        viewModelScope.launch {
            seriesRepository.updateSeriesReview(userId, seriesId, review, timestamp)
        }
    }

    @Suppress("UNUSED")
    suspend fun getSeriesReviews(seriesId: Long) = seriesRepository.getSeriesReviews(seriesId)
}

/**
 * Factory per la creazione di un [SeriesDetailsViewModel] con parametri personalizzati.
 *
 * @param seriesId id della serie tv
 *
 * @author nicolobartolinii
 */
class SeriesDetailsViewModelFactory(private val seriesId: Long) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SeriesDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return SeriesDetailsViewModel(seriesId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}