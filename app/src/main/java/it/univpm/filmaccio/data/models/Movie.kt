package it.univpm.filmaccio.data.models

import com.google.gson.annotations.SerializedName

// Data class che si occupa di racchiudere tutti i dettagli di un film che verranno poi utilizzati nella schermata di dettaglio del film (MovieDetailsActivity)
// Per il film memorizziamo: id, titolo, percorso TMDB del poster, trama, data di uscita, percorso TMDB dell'immagine backdrop, durata e i crediti (cast e crew)
// I crediti, come vedrete in TmdbApiService sono una chiamata aggiuntiva all'endpoint movie/{movie_id}, quindi in questo modo inseriamo anche i crediti nell'oggetto Movie con un'unica chiamata.
// I crediti, in questo caso, sono una classe interna a Movie che contengono due liste, una di Personaggi (guarda la classe Character) e una di Registi (guarda la classe Director).
// In realtà la lista crew non contiene solo registi, ma visto che a noi interessano solo i registi, poi a runtime filtriamo la lista crew e prendiamo solo i registi.

/**
 * Data class che si occupa di racchiudere tutti i dettagli di un film che verranno poi utilizzati
 * nella schermata di dettaglio del film (MovieDetailsActivity) e per altri scopi.
 *
 * @param id id del film
 * @param title titolo del film
 * @param posterPath percorso TMDB del poster
 * @param overview trama del film
 * @param releaseDate data di uscita del film
 * @param backdropPath percorso TMDB dell'immagine backdrop
 * @param duration durata del film
 * @param credits crediti del film (cast e crew)
 *
 * @see Character
 * @see Director
 *
 * @author nicolobartolinii
 */
data class Movie(
    @SerializedName("id") val id: Long,
    @SerializedName("title") var title: String,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("overview") var overview: String,
    @SerializedName("release_date") val releaseDate: String,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("runtime") val duration: Int,
    @SerializedName("credits") var credits: Credits,
) : java.io.Serializable {

    /**
     * Data class che rappresenta i crediti di un film.
     *
     * @param cast lista dei personaggi (attori e personaggi che interpretano)
     * @param crew lista dei membri della crew (registi, sceneggiatori, ecc.)
     *
     * @author nicolobartolinii
     */
    data class Credits(
        @SerializedName("cast") var cast: List<Character>,
        @SerializedName("crew") var crew: List<Director>
    ) : java.io.Serializable
}