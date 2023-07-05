package it.univpm.filmaccio.main.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.univpm.filmaccio.data.repository.MovieRepository
import it.univpm.filmaccio.data.repository.SeriesRepository
import it.univpm.filmaccio.main.utils.FirestoreService
import it.univpm.filmaccio.main.utils.UserUtils
import kotlinx.coroutines.launch
import kotlin.random.Random

class ChangeBackdropViewModel : ViewModel() {

    var backdropsTotal = listOf<String>()

    private val _backdrops = MutableLiveData<List<String>>()
    val backdrops: LiveData<List<String>>
        get() = _backdrops

    private val movieRepository = MovieRepository()
    private val seriesRepository = SeriesRepository()

    private val uid = UserUtils.getCurrentUserUid()

    var currentPage = 0
        set(value) {
            field = value
            _currentPage.value = value
        }
    val itemsPerPage = 20

    private var pages = 0
        set(value) {
            field = value
            _pages.value = value
        }

    private val _currentPage = MutableLiveData<Int>()
    val currentPageLiveData: LiveData<Int>
        get() = _currentPage

    private val _pages = MutableLiveData<Int>()
    val pagesLiveData: LiveData<Int>
        get() = _pages

    init {
        loadAllImages()
    }

    private fun loadAllImages() {
        viewModelScope.launch {
            val favoriteMovies = mutableListOf<Long>()
            FirestoreService.getList(uid!!, "favorite_m").collect { favoriteMovies.addAll(it) }
            val favoriteSeries = mutableListOf<Long>()
            FirestoreService.getList(uid, "favorite_t").collect { favoriteSeries.addAll(it) }

            val favoriteMoviesBackdrops = mutableListOf<String>()

            for (movie in favoriteMovies) {
                val movieImages = movieRepository.getMovieImages(movie)
                val backdrops = movieImages.backdrops
                val images =
                    backdrops.map { "https://image.tmdb.org/t/p/w1280/${it.filePath}" }.take(10)
                favoriteMoviesBackdrops.addAll(images)
            }

            val favoriteSeriesBackdrops = mutableListOf<String>()

            for (series in favoriteSeries) {
                val seriesImages = seriesRepository.getSeriesImages(series)
                val backdrops = seriesImages.backdrops
                val images =
                    backdrops.map { "https://image.tmdb.org/t/p/w1280/${it.filePath}" }.take(10)
                favoriteSeriesBackdrops.addAll(images)
            }

            backdropsTotal = (favoriteMoviesBackdrops + favoriteSeriesBackdrops).shuffled(Random(0))

            pages = kotlin.math.ceil(backdropsTotal.size / (itemsPerPage + 1.0)).toInt()
            loadNextPage()
        }
    }

    fun loadNextPage() {
        val start = currentPage * itemsPerPage
        val end = start + itemsPerPage
        if (end > backdropsTotal.size) {
            _backdrops.value = backdropsTotal.subList(start, backdropsTotal.size)
            this.currentPage++
            return
        }
        _backdrops.value = backdropsTotal.subList(start, end)
        this.currentPage++
    }

    fun loadPreviousPage() {
        if (currentPage == 0 || currentPage == 1) return
        this.currentPage--
        val end = currentPage * itemsPerPage
        val start = end - itemsPerPage
        _backdrops.value = backdropsTotal.subList(start, end)
    }
}
