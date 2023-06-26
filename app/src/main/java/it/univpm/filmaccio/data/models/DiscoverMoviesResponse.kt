package it.univpm.filmaccio.data.models

import com.google.gson.annotations.SerializedName

// Data class che rappresenta la risposta JSON della chiamata all'endpoint discover/movies.
// La chiamata all'endpoint nowPlaying, se guardate la sua documentazione nel sito di TMDB, non è altro che una chiamata all'endpoint discover con i parametri giusti.
// In altre parole chiamare nowPlaying è come chiamare discover ma senza dover specificare tutta una lunga serie di parametri, lo fa in automatico.
// Questo per far capire che la classe DiscoverMoviesResponse è la risposta JSON della chiamata all'endpoint nowPlaying che quindi deve essere mappata come una chiamata all'endpoint discover.
data class DiscoverMoviesResponse(
    @SerializedName("results") val movies: List<Movie>
)