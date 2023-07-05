package it.univpm.filmaccio.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Questa classe è un oggetto singleton che contiene il client per le chiamate alle API di TMDB
object TmdbApiClient {
    // URL base delle API di TMDB a cui poi vengono aggiunti i vari endpoint tramite Retrofit
    private const val BASE_URL = "https://api.themoviedb.org/3/"

    // Creazione del client tramite Retrofit, buildiamo il client con il base URL e un converter gson (è la pratica standard fare così)
    private val rf =
        Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create())
            .build()

    // Creazione del servizio tramite Retrofit, buildiamo il servizio con il client creato prima e la classe che contiene i metodi per le chiamate alle API (cioè i vari endpoint)
    val TMDB_API: TmdbApiService = rf.create(TmdbApiService::class.java)
}