package it.univpm.filmaccio.data.api

import it.univpm.filmaccio.data.models.DiscoverMoviesResponse
import it.univpm.filmaccio.data.models.DiscoverSeriesResponse
import it.univpm.filmaccio.data.models.Movie
import it.univpm.filmaccio.data.models.SearchResponse
import it.univpm.filmaccio.main.utils.Constants
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApiService {
    @GET("movie/now_playing")
    suspend fun getNowPlayingMovies(
        @Query("api_key") apiKey: String = Constants.TMDB_API_KEY,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "it-IT",
        @Query("region") region: String = "IT"
    ): DiscoverMoviesResponse

    @GET("trending/movie/week")
    suspend fun getTrendingMovies(
        @Query("api_key") apiKey: String = Constants.TMDB_API_KEY,
        @Query("language") language: String = "it-IT"
    ): DiscoverMoviesResponse

    @GET("trending/tv/week")
    suspend fun getTrendingSeries(
        @Query("api_key") apiKey: String = Constants.TMDB_API_KEY,
        @Query("language") language: String = "it-IT"
    ): DiscoverSeriesResponse

    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(
        @Query("api_key") apiKey: String = Constants.TMDB_API_KEY,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "it-IT",
        @Query("region") region: String = "IT"
    ): DiscoverMoviesResponse

    @GET("tv/top_rated")
    suspend fun getTopRatedSeries(
        @Query("api_key") apiKey: String = Constants.TMDB_API_KEY,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "it-IT",
        @Query("region") region: String = "IT"
    ): DiscoverSeriesResponse

    @GET("search/multi")
    suspend fun searchMulti(
        @Query("api_key") apiKey: String = Constants.TMDB_API_KEY,
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "it-IT",
        @Query("include_adult") includeAdult: Boolean = false
    ): SearchResponse

    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String = Constants.TMDB_API_KEY,
        @Query("language") language: String = "it-IT",
        @Query("region") region: String = "IT",
        @Query("append_to_response") appendToResponse: String = "credits"
    ): Movie
}
