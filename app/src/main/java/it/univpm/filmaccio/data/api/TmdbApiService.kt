package it.univpm.filmaccio.data.api

import it.univpm.filmaccio.data.models.MovieResponse
import it.univpm.filmaccio.main.utils.Constants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface TmdbApiService {
    @GET("movie/now_playing")
    suspend fun getNowPlayingMovies(@Query("api_key") apiKey: String = Constants.TMDB_API_KEY, @Query("page") page: Int = 1, @Query("language") language: String = "it-IT", @Query("region") region: String = "IT"): MovieResponse
}
