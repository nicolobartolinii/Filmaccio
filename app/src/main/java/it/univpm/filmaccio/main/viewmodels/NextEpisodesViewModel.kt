package it.univpm.filmaccio.main.viewmodels

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


/**
 * Questa classe è il ViewModel della schermata episodi dell'applicazione, quindi si occupa di gestire
 * i dati relativi agli episodi da vedere dell'utente corrente.
 *
 * @author nicolobartolinii
 */
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


    // Questo metodo calcola qual è il prossimo episodio da vedere per ogni serie che l'utente sta seguendo.
    // Per farlo, si basa sulle informazioni presenti nel database Firestore e sulle informazioni presenti
    // nel database TMDB.
    // In breve il suo funzionamento è il seguente:
    // 1. Si prende la lista delle serie che l'utente sta seguendo dal database Firestore.
    // 2. Si effettua un ciclo su ogni serie.
    // 3. Per ogni serie si rimuove l'eventuale stagione 0 (che essendoci a volte sì e a volte no, è
    // conveniente rimuoverla perché causa problemi, tanto si tratta della stagione degli speciali se c'è).
    // 4. Si ottengono i dettagli TMDB della serie (e anche qui si rimuove l'eventuale stagione 0).
    // 5. Si effettua un ciclo su ogni stagione presente nella lista watchingSeries di quella serie.
    // 6. Si ottengono i dettagli TMDB della stagione.
    // 7. Si effettuano una serie di controlli per capire se il prossimo episodio da guardare
    // è nella stagione corrente o in quella successiva, oppure se è in una stagione precedente o successiva
    // non ancora aggiunta alla lista watchingSeries.
    // 8. Una volta effettuati i cicli necessari e capito il numero della stagione e dell'episodio da guardare,
    // si aggiunge alla lista nextEpisodes un nuovo oggetto NextEpisode con i dati necessari.
    // 9. Si aggiorna la lista nextEpisodes.
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
            if (series.value.containsKey("0")) series.value.remove("0")
            if (series.value.isNotEmpty()) {
                for (season in series.value) {
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