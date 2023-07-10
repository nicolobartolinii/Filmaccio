package it.univpm.filmaccio.main.utils

import it.univpm.filmaccio.BuildConfig

/**
 * Questa classe contiene le costanti utilizzate in tutto il progetto.
 * In particolare, contiene la chiave API per l'accesso all'API di The Movie Database.
 * Il riferimento a tale chiave è contenuto nella classe BuildConfig, che è generata automaticamente
 * da Android Studio. Questo permette di non condividere la chiave API su GitHub.
 * Inoltre, contiene l'URL di default per lo sfondo del profilo.
 *
 * @author nicolobartolinii
 */
object Constants {
    const val TMDB_API_KEY = BuildConfig.TMDB_API_KEY
    const val DESERT_BACKDROP_URL =
        "https://firebasestorage.googleapis.com/v0/b/filmaccio.appspot.com/o/desert.jpg?alt=media&token=a2f60711-b962-40f9-9a8f-1b948e1cd92e"
}
