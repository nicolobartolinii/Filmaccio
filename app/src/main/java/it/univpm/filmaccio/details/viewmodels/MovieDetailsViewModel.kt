package it.univpm.filmaccio.details.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import it.univpm.filmaccio.data.repository.MovieRepository
import it.univpm.filmaccio.main.utils.UserUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MovieDetailsViewModel(private var movieId: Long = 0L) : ViewModel() {

    private val movieRepository = MovieRepository()

    private val userId = UserUtils.getCurrentUserUid()!!

    private val _isMovieWatched = MutableLiveData<Boolean>()
    val isMovieWatched: LiveData<Boolean> = _isMovieWatched

    private val _isMovieInWatchlist = MutableLiveData<Boolean>()
    val isMovieInWatchlist: LiveData<Boolean> = _isMovieInWatchlist

    private val _isMovieFavorited = MutableLiveData<Boolean>()
    val isMovieFavorited: LiveData<Boolean> = _isMovieFavorited

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
    }

    fun toggleWatched(movieId: Long) = viewModelScope.launch {
        if (_isMovieWatched.value == true) {
            movieRepository.removeFromList(userId, "watched_m", movieId)
            _isMovieWatched.value = false
        } else {
            movieRepository.addToList(userId, "watched_m", movieId)
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
}

class MovieDetailsViewModelFactory(private val movieId: Long) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MovieDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MovieDetailsViewModel(movieId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}