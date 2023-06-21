package it.univpm.filmaccio.main.utils

import it.univpm.filmaccio.BuildConfig

// Questo è un modo che mi ha consigliato ChatGPT per salvare la chiave API in modo sicuro senza
// condividerla su GitHub.
object Constants {
    const val TMDB_API_KEY = BuildConfig.TMDB_API_KEY
}
