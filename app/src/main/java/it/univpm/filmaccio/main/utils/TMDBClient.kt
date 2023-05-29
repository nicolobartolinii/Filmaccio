package it.univpm.filmaccio.main.utils

import app.moviebase.tmdb.Tmdb3

object TMDBClient {
    val tmdb = Tmdb3 {
        tmdbApiKey = Constants.TMDB_API_KEY

        expectSuccess = false // if you want to disable exceptions
        useCache = true
        useTimeout = true
        maxRetriesOnException = 3
    }
}