package it.univpm.filmaccio.data.api

import it.univpm.filmaccio.data.models.DiscoverMoviesResponse
import it.univpm.filmaccio.data.models.DiscoverSeriesResponse
import it.univpm.filmaccio.data.models.Movie
import it.univpm.filmaccio.data.models.Person
import it.univpm.filmaccio.data.models.SearchResponse
import it.univpm.filmaccio.data.models.Series
import it.univpm.filmaccio.main.utils.Constants
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

// Questa interfaccia contiene tutti gli endpoint delle API di TMDB che ci servono per l'applicazione.
// Questa classe mostra perfettamente come si usa Retrofit per le chiamate alle API.
// Come vedete ogni metodo ha un'annotazione @GET che indica il tipo di richiesta HTTP che viene fatta.
// Inoltre, ogni metodo ha la parola chiave suspend che indica che il metodo è sospensivo (asincrono).
// Questo perché le chiamate alle API devono essere asincrone, altrimenti l'applicazione si bloccherebbe.
// Inoltre, ogni metodo ha una lista di parametri che vengono aggiunti all'URL della richiesta.
// Quando si usa @Query significa che il parametro viene aggiunto all'URL come query parameter (ad
// esempio ?api_key=...), mentre quando si usa @Path significa che il parametro viene aggiunto all'URL
// di chiamata come path parameter (ad esempio /movie/{movie_id}).
interface TmdbApiService {

    // Endpoint per ottenere la lista dei film attualmente al cinema
    // Come ho spiegato in DiscoverMoviesResponse questa è una chiamata a discover camuffata
    @GET("movie/now_playing")
    suspend fun getNowPlayingMovies(
        @Query("api_key") apiKey: String = Constants.TMDB_API_KEY,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "it-IT",
        @Query("region") region: String = "IT"
    ): DiscoverMoviesResponse

    // Endpoint per ottenere la lista dei film attualmente di tendenza
    // Anche questa è una chiamata a discover camuffata
    @GET("trending/movie/week")
    suspend fun getTrendingMovies(
        @Query("api_key") apiKey: String = Constants.TMDB_API_KEY,
        @Query("language") language: String = "it-IT"
    ): DiscoverMoviesResponse

    // Endpoint per ottenere la lista delle serie TV attualmente di tendenza
    // Anche questa è una chiamata a discover camuffata
    @GET("trending/tv/week")
    suspend fun getTrendingSeries(
        @Query("api_key") apiKey: String = Constants.TMDB_API_KEY,
        @Query("language") language: String = "it-IT"
    ): DiscoverSeriesResponse

    // Endpoint per ottenere la lista dei film con le votazioni più alte di TMDB.
    // Questa chiamta la sto usando per rimpiazzare temporaneamente la lista dei film CONSIGLIATI
    // nella schermata di esplorazione (schermata di ricerca). Questo perché senza avere una lista
    // di film visti dall'utente ben strutturata (ormai c'è) non avevo ancora modo di implementare
    // l'algoritmo di raccomandazione.
    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(
        @Query("api_key") apiKey: String = Constants.TMDB_API_KEY,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "it-IT",
        @Query("region") region: String = "IT"
    ): DiscoverMoviesResponse

    // Stessa cosa per l'endpoint sopra ma per le serie TV
    @GET("tv/top_rated")
    suspend fun getTopRatedSeries(
        @Query("api_key") apiKey: String = Constants.TMDB_API_KEY,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "it-IT",
        @Query("region") region: String = "IT"
    ): DiscoverSeriesResponse

    // Endpoint per ricercare film, serie TV e persone in base ad una query di ricerca.
    @GET("search/multi")
    suspend fun searchMulti(
        @Query("api_key") apiKey: String = Constants.TMDB_API_KEY,
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "it-IT",
        @Query("include_adult") includeAdult: Boolean = false
    ): SearchResponse

    // Endpoint per ottenere i dettagli di un film
    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: Long,
        @Query("api_key") apiKey: String = Constants.TMDB_API_KEY,
        @Query("language") language: String = "it-IT",
        @Query("region") region: String = "IT",
        @Query("append_to_response") appendToResponse: String = "credits"
    ): Movie

    // Endpoint per ottenere i dettagli di una serie TV
    @GET("tv/{series_id}")
    suspend fun getSeriesDetails(
        @Path("series_id") seriesId: Long,
        @Query("api_key") apiKey: String = Constants.TMDB_API_KEY,
        @Query("language") language: String = "it-IT",
        @Query("region") region: String = "IT",
        @Query("append_to_response") appendToResponse: String = "credits"
    ): Series

    // Endpoint per ottenere i dettagli di una stagione di una serie TV
    @GET("tv/{series_id}/season/{season_number}")
    suspend fun getSeasonDetails(
        @Path("series_id") seriesId: Long,
        @Path("season_number") seasonNumber: Int,
        @Query("api_key") apiKey: String = Constants.TMDB_API_KEY,
        @Query("language") language: String = "it-IT",
        @Query("region") region: String = "IT",
    ): Series.Season

    // Endpoint per ottenere i dettagli di una persona
    @GET("person/{person_id}")
    suspend fun getPersonDetails(
        @Path("person_id") personId: Long,
        @Query("api_key") apiKey: String = Constants.TMDB_API_KEY,
        @Query("language") language: String = "it-IT",
        @Query("region") region: String = "IT",
        @Query("append_to_response") appendToResponse: String = "combined_credits"
    ): Person
}
