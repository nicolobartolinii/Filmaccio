package it.univpm.filmaccio.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import it.univpm.filmaccio.data.repository.SeriesRepository
import kotlinx.coroutines.Dispatchers

class SeriesDetailsViewModel(private var seriesId: Int = 0) : ViewModel() {
    private val seriesRepository = SeriesRepository()

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