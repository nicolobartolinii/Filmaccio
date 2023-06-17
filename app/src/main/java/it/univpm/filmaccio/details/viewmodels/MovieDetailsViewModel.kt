package it.univpm.filmaccio.details.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import it.univpm.filmaccio.data.repository.MovieRepository
import it.univpm.filmaccio.main.utils.UserUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MovieDetailsViewModel(private var movieId: Int = 0) : ViewModel() {

    private val movieRepository = MovieRepository()

    private val userId = UserUtils.getCurrentUserUid()!!

    val isMovieWatched: LiveData<Boolean> = liveData {
        emit(movieRepository.isMovieWatched(userId, movieId))
    }

    val isMovieInWatchlist: LiveData<Boolean> = liveData {
        emit(movieRepository.isMovieInWatchlist(userId, movieId))
    }

    val isMovieFavorited: LiveData<Boolean> = liveData {
        emit(movieRepository.isMovieFavorited(userId, movieId))
    }

    fun toggleWatched(movieId: Int) = viewModelScope.launch {
        if (movieRepository.isMovieWatched(userId, movieId)) {
            movieRepository.removeFromList(userId, "watched", movieId)
        } else {
            movieRepository.addToList(userId, "watched", movieId)
            if (movieRepository.isMovieInWatchlist(userId, movieId)) {
                movieRepository.removeFromList(userId, "watchlist", movieId)
            }
        }
    }

    fun toggleWatchlist(movieId: Int) = viewModelScope.launch {
        if (movieRepository.isMovieInWatchlist(userId, movieId)) {
            movieRepository.removeFromList(userId, "watchlist", movieId)
        } else {
            movieRepository.addToList(userId, "watchlist", movieId)
        }
    }

    fun toggleFavorite(movieId: Int) = viewModelScope.launch {
        if (movieRepository.isMovieFavorited(userId, movieId)) {
            movieRepository.removeFromList(userId, "favorite", movieId)
        } else {
            movieRepository.addToList(userId, "favorite", movieId)
        }
    }


    val currentMovie = liveData(Dispatchers.IO) {
        val movie = movieRepository.getMovieDetails(movieId)
        emit(movie)
    }
}

class MovieDetailsViewModelFactory(private val movieId: Int) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MovieDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MovieDetailsViewModel(movieId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}