package it.univpm.filmaccio.main.utils

import it.univpm.filmaccio.BuildConfig

// Questo è un modo che mi ha consigliato ChatGPT per salvare la chiave API in modo sicuro senza
// condividerla su GitHub.
object Constants {
    const val TMDB_API_KEY = BuildConfig.TMDB_API_KEY
    const val DESERT_BACKDROP_URL = "https://firebasestorage.googleapis.com/v0/b/filmaccio-1e9f0.appspot.com/o/desert.jpg?alt=media&token=8b5b8b1a-4b9e-4b9e-8b9e-4b9e8b5b8b1a"
}
