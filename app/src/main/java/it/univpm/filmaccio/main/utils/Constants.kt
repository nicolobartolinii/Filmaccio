package it.univpm.filmaccio.main.utils

import it.univpm.filmaccio.BuildConfig

// Questo è un modo che mi ha consigliato ChatGPT per salvare la chiave API in modo sicuro senza
// condividerla su GitHub.
object Constants {
    const val TMDB_API_KEY = BuildConfig.TMDB_API_KEY
    const val DESERT_BACKDROP_URL =
        "https://firebasestorage.googleapis.com/v0/b/filmaccio.appspot.com/o/desert.jpg?alt=media&token=a2f60711-b962-40f9-9a8f-1b948e1cd92e"
}
