package it.univpm.filmaccio.main.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.univpm.filmaccio.data.models.NextEpisode
import it.univpm.filmaccio.data.repository.SeriesRepository
import it.univpm.filmaccio.main.utils.FirestoreService
import it.univpm.filmaccio.main.utils.UserUtils
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Collections

class NextEpisodesViewModel : ViewModel() {

    val uid = UserUtils.getCurrentUserUid()!!

    val seriesRepository = SeriesRepository()

    private var _nextEpisodes = MutableLiveData<List<NextEpisode>>(null)
    val nextEpisodes: LiveData<List<NextEpisode>> get() = _nextEpisodes

    init {
        viewModelScope.launch {
            loadNextEpisodes()
        }
    }

    private suspend fun loadNextEpisodes() {
        val watchingSeries = FirestoreService.getWatchingSeries(uid).first()
        var nextEpisodesList = listOf<NextEpisode>()
        for (series in watchingSeries) {
            var seasonNumber = -1L
            var nextEpisodeNumber = -1L
            val seriesId = series.key.toLong()
            val seriesDetails = seriesRepository.getSeriesDetails(seriesId)
            if (seriesDetails.seasons[0].number == 0L) {
                Collections.rotate(seriesDetails.seasons, -1)
            }
//            if (series.value.containsKey("0")) {
//                val season0 = series.value["0"]
//                series.value.remove("0")
//            }
            for (season in series.value) {
                seasonNumber = season.key.toLong()
                val seasonDetails = seriesRepository.getSeasonDetails(seriesId, seasonNumber)
                if (season.value.size == seasonDetails.episodes.size) continue
                else {
                    nextEpisodeNumber = season.value.last().toLong() + 1L
                    if (nextEpisodeNumber == seasonDetails.episodes.size.toLong() + 1L) {
                        nextEpisodeNumber = 1
                        while (season.value.contains(nextEpisodeNumber)) {
                            nextEpisodeNumber++
                        }
                    }
                }
                if (nextEpisodeNumber != -1L) break
            }
            Log.d("NextEpisodesViewModel", "seriesDetails: $seriesDetails")
            Log.d("NextEpisodesViewModel", "seasonNumber: $seasonNumber")
            Log.d("NextEpisodesViewModel", "nextEpisodeNumber: $nextEpisodeNumber")
            val nextEpisode = NextEpisode(seriesId, seriesDetails.title, seasonNumber, nextEpisodeNumber, seriesDetails.seasons[seasonNumber.toInt() - 1].episodes[nextEpisodeNumber.toInt() - 1].name, seriesDetails.posterPath)
            nextEpisodesList = nextEpisodesList + nextEpisode
        }
        _nextEpisodes.value = nextEpisodesList
    }
}