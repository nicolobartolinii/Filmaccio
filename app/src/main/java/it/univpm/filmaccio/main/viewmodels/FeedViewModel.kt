package it.univpm.filmaccio.main.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import it.univpm.filmaccio.data.models.Person
import it.univpm.filmaccio.data.models.ReviewTriple
import it.univpm.filmaccio.data.models.TmdbEntity
import it.univpm.filmaccio.data.repository.MovieRepository
import it.univpm.filmaccio.data.repository.PeopleRepository
import it.univpm.filmaccio.data.repository.SeriesRepository
import it.univpm.filmaccio.main.utils.FirestoreService
import it.univpm.filmaccio.main.utils.UserUtils
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Questa classe è il ViewModel della schermata feed dell'applicazione, quindi si occupa di gestire
 * i dati relativi alle recensioni e alle persone relative all'utente corrente.
 *
 * @author nicolobartolinii
 */
class FeedViewModel : ViewModel() {

    private val peopleRepository = PeopleRepository()
    private val movieRepository = MovieRepository()
    private val seriesRepository = SeriesRepository()

    private val userId = UserUtils.getCurrentUserUid()!!

    private val _usersFeed = MutableLiveData<List<Pair<ReviewTriple, TmdbEntity>>>(null)
    val usersFeed: LiveData<List<Pair<ReviewTriple, TmdbEntity>>> = _usersFeed

    private val _followedPeople = MutableLiveData<List<Person>>(null)
    val followedPeople: LiveData<List<Person>> = _followedPeople

    init {
        viewModelScope.launch {
            loadFollowedPeople()
        }
        viewModelScope.launch {
            loadUsersFeed()
        }
    }

    suspend fun loadUsersFeed() {
        val following = FirestoreService.getFollowing(userId).first()
        val reviews = mutableListOf<Pair<ReviewTriple, TmdbEntity>>()
        for (user in following) {
            val movieReview = movieRepository.getUserReviews(user)
            val seriesReview = seriesRepository.getUserReviews(user)
            if (movieReview == null && seriesReview != null) {
                val series = seriesRepository.getSeriesDetails(seriesReview.second)
                val tmdbEntity = TmdbEntity(series.id, series.title, series.posterPath, "series")
                reviews.add(Pair(seriesReview.first, tmdbEntity))
            } else if (movieReview != null && seriesReview == null) {
                val movie = movieRepository.getMovieDetails(movieReview.second)
                val tmdbEntity = TmdbEntity(movie.id, movie.title, movie.posterPath, "movie")
                reviews.add(Pair(movieReview.first, tmdbEntity))
            } else if (movieReview != null && seriesReview != null) {
                val format = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ITALY)
                val movieDate = format.parse(movieReview.first.date)!!
                val movieTimestamp = Timestamp(movieDate)
                val seriesDate = format.parse(seriesReview.first.date)!!
                val seriesTimestamp = Timestamp(seriesDate)
                if (movieTimestamp > seriesTimestamp && movieReview.second != 0L) {
                    val movie = movieRepository.getMovieDetails(movieReview.second)
                    val tmdbEntity = TmdbEntity(movie.id, movie.title, movie.posterPath, "movie")
                    reviews.add(Pair(movieReview.first, tmdbEntity))
                } else if (seriesTimestamp > movieTimestamp && seriesReview.second != 0L) {
                    val series = seriesRepository.getSeriesDetails(seriesReview.second)
                    val tmdbEntity =
                        TmdbEntity(series.id, series.title, series.posterPath, "series")
                    reviews.add(Pair(seriesReview.first, tmdbEntity))
                }
            }
        }
        _usersFeed.value = reviews
    }

    suspend fun loadFollowedPeople() {
        _followedPeople.value = peopleRepository.getFollowedPeople(userId)
    }
}