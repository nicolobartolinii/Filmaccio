package it.univpm.filmaccio.data.models

import com.google.gson.annotations.SerializedName

/**
 * Data class che rappresenta i dettagli di una serie TV.
 * Per i credits fare riferimento alla classe Movie.
 *
 * @see Movie
 *
 * @param id id della serie TV
 * @param title titolo della serie TV
 * @param posterPath path del poster della serie TV
 * @param overview trama della serie TV
 * @param releaseDate data di uscita della serie TV
 * @param backdropPath path del backdrop della serie TV
 * @param duration durata della serie TV
 * @param credits crediti della serie TV
 * @param creator lista dei creatori della serie TV
 * @param seasons lista delle stagioni della serie TV
 *
 * @author nicolobartolinii
 */
data class Series(
    @SerializedName("id") val id: Long,
    @SerializedName("name") var title: String,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("overview") var overview: String,
    @SerializedName("first_air_date") val releaseDate: String,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("number_of_episodes") val duration: Int,
    @SerializedName("credits") var credits: Credits,
    @SerializedName("created_by") var creator: List<Director>,
    @SerializedName("seasons") var seasons: List<Season>
) : java.io.Serializable {
    data class Credits(
        @SerializedName("cast") var cast: List<Character>,
        @SerializedName("crew") var crew: List<Director>
    ) : java.io.Serializable

    /**
     * Per ogni serie TV l'API ci dà una lista di stagioni (facendo una chiamata combinata con
     * append_to_response) e per ogni stagione abbiamo una lista di episodi.
     *
     * @param number numero della stagione
     * @param releaseDate data di uscita della stagione
     * @param posterPath path del poster della stagione
     * @param overview trama della stagione
     * @param episodes lista degli episodi della stagione
     * @param name nome della stagione
     *
     * @author nicolobartolinii
     */
    data class Season(
        @SerializedName("season_number") var number: Long,
        @SerializedName("air_date") var releaseDate: String?,
        @SerializedName("poster_path") var posterPath: String?,
        @SerializedName("overview") var overview: String,
        @SerializedName("episodes") var episodes: List<Episode> = emptyList(),
        @SerializedName("name") var name: String
    ) : java.io.Serializable

    /**
     * Per ogni stagione abbiamo una lista di episodi.
     *
     * @param number numero dell'episodio
     * @param duration durata dell'episodio
     * @param releaseDate data di uscita dell'episodio
     * @param imagePath path dell'immagine dell'episodio
     * @param overview trama dell'episodio
     * @param name nome dell'episodio
     *
     * @author nicolobartolinii
     */
    data class Episode(
        @SerializedName("episode_number") val number: Long,
        @SerializedName("runtime") val duration: Int,
        @SerializedName("air_date") val releaseDate: String,
        @SerializedName("still_path") val imagePath: String?,
        @SerializedName("overview") var overview: String,
        @SerializedName("name") var name: String
    ) : java.io.Serializable
}