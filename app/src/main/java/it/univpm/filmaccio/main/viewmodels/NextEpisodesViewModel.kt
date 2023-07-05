package it.univpm.filmaccio.main.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.univpm.filmaccio.data.models.NextEpisode
import it.univpm.filmaccio.data.models.Series
import it.univpm.filmaccio.data.repository.SeriesRepository
import it.univpm.filmaccio.main.utils.FirestoreService
import it.univpm.filmaccio.main.utils.UserUtils
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class NextEpisodesViewModel : ViewModel() {

    val uid = UserUtils.getCurrentUserUid()!!

    private val seriesRepository = SeriesRepository()

    private var _nextEpisodes = MutableLiveData<List<NextEpisode>>(null)
    val nextEpisodes: LiveData<List<NextEpisode>> get() = _nextEpisodes

    init {
        viewModelScope.launch {
            loadNextEpisodes()
        }
    }

    @Suppress("UNCHECKED_CAST")
    suspend fun loadNextEpisodes() {
        val watchingSeries = FirestoreService.getWatchingSeries(uid)
            .first() as MutableMap<String, MutableMap<String, List<Long>>>? ?: mutableMapOf()
        var nextEpisodesList = listOf<NextEpisode>()
        for (series in watchingSeries) {
            var seasonNumber = -1L
            var nextEpisodeNumber = -1L
            val seriesId = series.key.toLong()
            val seriesDetails = seriesRepository.getSeriesDetails(seriesId)
            (seriesDetails.seasons as MutableList<Series.Season>).removeIf { it.number == 0L }
            Log.d("NextEpisodesViewModel", "Series: $series")
            if (series.value.containsKey("0")) series.value.remove("0")
            if (series.value.isNotEmpty()) {
                for (season in series.value) {
                    Log.d(
                        "NextEpisodesViewModel",
                        "Season: $season, Series name: ${seriesDetails.title}"
                    )
                    seasonNumber = season.key.toLong()
                    val seasonDetails = seriesRepository.getSeasonDetails(seriesId, seasonNumber)
                    if (season.value.size == seasonDetails.episodes.size) {
                        if (series.value.size != seriesDetails.seasons.size && !series.value.containsKey(
                                (seasonNumber + 1L).toString()
                            )
                        ) {
                            FirestoreService.addSeasonToWatchingSeries(
                                uid, seriesId, seasonNumber + 1L
                            )
                            seasonNumber++
                            nextEpisodeNumber = 1L
                            break
                        } else continue
                    } else {
                        if (season.key.toLong() != 1L && !series.value.containsKey("1")) {
                            seasonNumber = 1L
                            while (series.value.containsKey(seasonNumber.toString())) {
                                seasonNumber++
                            }
                            FirestoreService.addSeasonToWatchingSeries(uid, seriesId, seasonNumber)
                            nextEpisodeNumber = 1L
                            break
                        } else {
                            nextEpisodeNumber =
                                if (season.value.isNotEmpty()) season.value.max().toLong() + 1L
                                else 1L
                            if (nextEpisodeNumber == seasonDetails.episodes.size.toLong() + 1L) {
                                nextEpisodeNumber = 1L
                                while (season.value.contains(nextEpisodeNumber)) {
                                    nextEpisodeNumber++
                                }
                            }
                        }
                    }
                    if (nextEpisodeNumber != -1L && season.value.size == seriesDetails.seasons[seasonNumber.toInt() - 1].episodes.size) continue
                    else if (nextEpisodeNumber != -1L && series.value.size == seriesDetails.seasons.size) break
                    else break
                }
            } else {
                seasonNumber = 1L
                nextEpisodeNumber = 1L
            }
            val nextEpisode = if (nextEpisodeNumber != -1L) NextEpisode(
                seriesId,
                seriesDetails.title,
                seasonNumber,
                nextEpisodeNumber,
                seriesDetails.seasons[seasonNumber.toInt() - 1].episodes[nextEpisodeNumber.toInt() - 1].name,
                seriesDetails.posterPath,
                seriesDetails.seasons[seasonNumber.toInt() - 1].episodes[nextEpisodeNumber.toInt() - 1].duration.toLong()
            )
            else continue
            nextEpisodesList = nextEpisodesList + nextEpisode
        }
        _nextEpisodes.value = nextEpisodesList
    }
}