package it.univpm.filmaccio.details.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import it.univpm.filmaccio.data.models.ReviewTriple
import it.univpm.filmaccio.data.repository.MovieRepository
import it.univpm.filmaccio.data.repository.UsersRepository
import it.univpm.filmaccio.main.utils.UserUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * ViewModel per la gestione dei dettagli di un film
 *
 * @param movieId id del film
 *
 * @author nicolobartolinii
 */
class MovieDetailsViewModel(private var movieId: Long = 0L) : ViewModel() {

    private val movieRepository = MovieRepository()

    private val usersRepository = UsersRepository()

    private val userId = UserUtils.getCurrentUserUid()!!

    private val _isMovieWatched = MutableLiveData<Boolean>()
    val isMovieWatched: LiveData<Boolean> = _isMovieWatched

    private val _isMovieInWatchlist = MutableLiveData<Boolean>()
    val isMovieInWatchlist: LiveData<Boolean> = _isMovieInWatchlist

    private val _isMovieFavorited = MutableLiveData<Boolean>()
    val isMovieFavorited: LiveData<Boolean> = _isMovieFavorited

    private val _isMovieRated = MutableLiveData<Boolean>()
    val isMovieRated: LiveData<Boolean> = _isMovieRated

    private val _isMovieReviewed = MutableLiveData<Boolean>()
    val isMovieReviewed: LiveData<Boolean> = _isMovieReviewed

    private val _currentMovieRating = MutableLiveData<Pair<Float, Timestamp>?>(null)
    val currentMovieRating: LiveData<Pair<Float, Timestamp>?> = _currentMovieRating

    private val _currentMovieReview = MutableLiveData<Pair<String, Timestamp>?>(null)
    val currentMovieReview: LiveData<Pair<String, Timestamp>?> = _currentMovieReview

    private val _averageMovieRating = MutableLiveData<Float>()
    val averageMovieRating: LiveData<Float> = _averageMovieRating

    private val _movieReviews = MutableLiveData<List<ReviewTriple>>(emptyList())
    val movieReviews: LiveData<List<ReviewTriple>> = _movieReviews

    init {
        viewModelScope.launch {
            _isMovieWatched.value = movieRepository.isMovieWatched(userId, movieId)
        }
        viewModelScope.launch {
            _isMovieInWatchlist.value = movieRepository.isMovieInWatchlist(userId, movieId)
        }
        viewModelScope.launch {
            _isMovieFavorited.value = movieRepository.isMovieFavorited(userId, movieId)
        }
        viewModelScope.launch {
            _isMovieRated.value = movieRepository.isMovieRated(userId, movieId)
        }
        viewModelScope.launch {
            _isMovieReviewed.value = movieRepository.isMovieReviewed(userId, movieId)
        }
        viewModelScope.launch {
            _averageMovieRating.value = movieRepository.getAverageMovieRating(movieId)
        }
        viewModelScope.launch {
            _movieReviews.value = movieRepository.getMovieReviews(movieId)
        }
    }

    fun toggleWatched(movieId: Long) = viewModelScope.launch {
        if (_isMovieWatched.value == true) {
            movieRepository.removeFromList(userId, "watched_m", movieId)
            usersRepository.updateUserField(
                userId,
                "movieMinutes",
                FieldValue.increment(-currentMovie.value!!.duration.toLong())
            ) {}
            usersRepository.updateUserField(userId, "moviesNumber", FieldValue.increment(-1)) {}
            _isMovieWatched.value = false
        } else {
            movieRepository.addToList(userId, "watched_m", movieId)
            usersRepository.updateUserField(
                userId,
                "movieMinutes",
                FieldValue.increment(currentMovie.value!!.duration.toLong())
            ) {}
            usersRepository.updateUserField(userId, "moviesNumber", FieldValue.increment(1)) {}
            if (_isMovieInWatchlist.value == true) {
                movieRepository.removeFromList(userId, "watchlist_m", movieId)
                _isMovieInWatchlist.value = false
            }
            _isMovieWatched.value = true
        }
    }

    fun toggleWatchlist(movieId: Long) = viewModelScope.launch {
        if (_isMovieInWatchlist.value == true) {
            movieRepository.removeFromList(userId, "watchlist_m", movieId)
            _isMovieInWatchlist.value = false
        } else {
            movieRepository.addToList(userId, "watchlist_m", movieId)
            _isMovieInWatchlist.value = true
        }
    }

    fun toggleFavorite(movieId: Long) = viewModelScope.launch {
        if (_isMovieFavorited.value == true) {
            movieRepository.removeFromList(userId, "favorite_m", movieId)
            _isMovieFavorited.value = false
        } else {
            movieRepository.addToList(userId, "favorite_m", movieId)
            _isMovieFavorited.value = true
        }
    }


    val currentMovie = liveData(Dispatchers.IO) {
        val movie = movieRepository.getMovieDetails(movieId)
        emit(movie)
    }

    fun loadCurrentMovieRating(userId: String, movieId: Long) {
        viewModelScope.launch {
            _currentMovieRating.value = movieRepository.getMovieRating(userId, movieId)
        }
    }

    fun loadCurrentMovieReview(userId: String, movieId: Long) {
        viewModelScope.launch {
            _currentMovieReview.value = movieRepository.getMovieReview(userId, movieId)
        }
    }

    fun updateMovieRating(userId: String, movieId: Long, rating: Float, timestamp: Timestamp) {
        viewModelScope.launch {
            movieRepository.updateMovieRating(userId, movieId, rating, timestamp)
        }
    }

    fun updateMovieReview(userId: String, movieId: Long, review: String, timestamp: Timestamp) {
        viewModelScope.launch {
            movieRepository.updateMovieReview(userId, movieId, review, timestamp)
        }
    }

    @Suppress("UNUSED")
    suspend fun getMovieReviews(movieId: Long) = movieRepository.getMovieReviews(movieId)
}

/**
 * Factory per la creazione di un MovieDetailsViewModel con parametri personalizzati
 *
 * @param movieId id del film
 *
 * @author nicolobartolinii
 */
class MovieDetailsViewModelFactory(private val movieId: Long) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MovieDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return MovieDetailsViewModel(movieId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}