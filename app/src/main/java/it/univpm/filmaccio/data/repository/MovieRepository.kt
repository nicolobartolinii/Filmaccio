package it.univpm.filmaccio.data.repository

import it.univpm.filmaccio.data.api.TmdbApiClient
import it.univpm.filmaccio.data.models.DiscoverMoviesResponse
import it.univpm.filmaccio.data.models.ImagesResponse
import it.univpm.filmaccio.data.models.Movie
import it.univpm.filmaccio.data.models.ProfileListItem
import it.univpm.filmaccio.main.utils.FirestoreService
import kotlinx.coroutines.flow.first

// Questa classe è un repository che si occupa di gestire i dati relativi ai film.
// In particolare si occupa di fare le chiamate all'API di TMDB per ottenere i dati relativi ai film e
// di gestire i dati relativi alle liste di film dell'utente.
class MovieRepository {

    // Qui creiamo un oggetto che contiene il client per le chiamate alle API di TMDB
    private val tmdbApi = TmdbApiClient.TMDB_API

    // Questo metodo implementa la chiamata all'endpoint nowPlaying in modo da poterlo usare in altre classi
    suspend fun getNowPlayingMovies(
        page: Int = 1,
        language: String = "it-IT",
        region: String = "IT"
    ): DiscoverMoviesResponse {
        return tmdbApi.getNowPlayingMovies(page = page, language = language, region = region)
    }

    // Questo metodo implementa la chiamata all'endpoint trending/movie/week in modo da poterlo usare in altre classi
    suspend fun getTrendingMovies(language: String = "it-IT"): DiscoverMoviesResponse {
        return tmdbApi.getTrendingMovies(language = language)
    }

    // Questo metodo implementa la chiamata all'endpoint movie/top_rated in modo da poterlo usare in altre classi
    suspend fun getTopRatedMovies(
        page: Int = 1,
        language: String = "it-IT",
        region: String = "IT"
    ): DiscoverMoviesResponse {
        return tmdbApi.getTopRatedMovies(page = page, language = language, region = region)
    }

    // Questo metodo implementa la chiamata all'endpoint di dettaglio dei film in modo da poterlo usare in altre classi
    suspend fun getMovieDetails(movieId: Long): Movie {
        return tmdbApi.getMovieDetails(movieId = movieId)
    }

    // Questo metodo si aggancia all'oggetto FirestoreService per aggiungere un film ad una lista dell'utente
    fun addToList(userId: String, listName: String, movieId: Long) {
        FirestoreService.addToList(userId, listName, movieId)
    }

    // Questo metodo si aggancia all'oggetto FirestoreService per rimuovere un film da una lista dell'utente
    fun removeFromList(userId: String, listName: String, movieId: Long) {
        FirestoreService.removeFromList(userId, listName, movieId)
    }

    // Questo metodo si aggancia all'oggetto FirestoreService per verificare se un film è presente nella lista dei film
    // visti dall'utente
    suspend fun isMovieWatched(userId: String, movieId: Long): Boolean {
        val watchedMovies: List<Any> = FirestoreService.getList(userId, "watched_m").first()
        return movieId in watchedMovies
    }

    // Questo metodo si aggancia all'oggetto FirestoreService per verificare se un film è presente nella lista dei film
    // presenti nella watchlist dell'utente
    suspend fun isMovieInWatchlist(userId: String, movieId: Long): Boolean {
        val watchlistMovies: List<Any> = FirestoreService.getList(userId, "watchlist_m").first()
        return movieId in watchlistMovies
    }

    // Questo metodo si aggancia all'oggetto FirestoreService per verificare se un film è presente nella lista dei film
    // preferiti dall'utente
    suspend fun isMovieFavorited(userId: String, movieId: Long): Boolean {
        val favoriteMovies: List<Any> = FirestoreService.getList(userId, "favorite_m").first()
        return movieId in favoriteMovies
    }

    // Questo metodo implementa la chiamata all'endpoint movie/{movie_id}/images in modo da poterlo usare in altre classi
    suspend fun getMovieImages(movieId: Long): ImagesResponse {
        return tmdbApi.getMovieImages(movieId = movieId)
    }

    // Questo metodo si occupa di convertire le informazioni di una lista di film da mostrare nella schermata profilo
    // in un oggetto di tipo ProfileListItem. In particolare, passiamo gli id dei primi tre film della lista e il suo nome
    // e otteniamo un oggetto ProfileListItem che contiene il nome della lista e le url dei poster dei primi tre film
    suspend fun convertIdToProfileListItem(
        id1: Long,
        id2: Long,
        id3: Long,
        listTitle: String
    ): ProfileListItem {
        // Qui convertiamo il nome della lista in modo da poterlo mostrare nella schermata profilo
        val listName = when (listTitle) {
            "watched_m" -> "visti (film)__"
            "watchlist_m" -> "watchlist (film)__"
            "favorite_m" -> "preferiti (film)__"
            else -> listTitle
        }
        // Qui controlliamo se la lista è vuota e in caso affermativo restituiamo un oggetto ProfileListItem con
        // il titolo della lista e tre url vuote
        if (id1 == 0L) {
            return ProfileListItem(
                title = listName.substring(0, listName.length - 2).uppercase(),
                imageURL1 = "",
                imageURL2 = "",
                imageURL3 = ""
            )
        }
        // Se la lista non è vuota, otteniamo i dettagli del primo film
        val movie1 = getMovieDetails(id1)
        // Qui controlliamo se la lista contiene un solo film e in caso affermativo restituiamo un oggetto ProfileListItem
        // con il titolo della lista e l'url del poster del primo film e gli altri due poster vuoti
        if (id2 == 0L) {
            return ProfileListItem(
                title = listName.substring(0, listName.length - 2).uppercase(),
                imageURL1 = movie1.posterPath ?: "",
                imageURL2 = "",
                imageURL3 = ""
            )
        }
        // Se la lista contiene due film, otteniamo i dettagli del secondo film
        val movie2 = getMovieDetails(id2)
        // Qui controlliamo se la lista contiene esattamente due film e in caso affermativo resituiamo un oggetto
        // ProfileListItem con il titolo della lista e gli url dei poster dei primi due film e l'ultimo poster vuoto
        if (id3 == 0L) {
            return ProfileListItem(
                title = listName.substring(0, listName.length - 2).uppercase(),
                imageURL1 = movie1.posterPath ?: "",
                imageURL2 = movie2.posterPath ?: "",
                imageURL3 = ""
            )
        }
        // Se la lista contiene tre film, otteniamo i dettagli del terzo film
        val movie3 = getMovieDetails(id3)
        // Infine, restituiamo un oggetto ProfileListItem con il titolo della lista e gli url dei poster dei tre film
        return ProfileListItem(
            title = listName.substring(0, listName.length - 2).uppercase(),
            imageURL1 = movie1.posterPath ?: "",
            imageURL2 = movie2.posterPath ?: "",
            imageURL3 = movie3.posterPath ?: ""
        )
    }
}