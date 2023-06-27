package it.univpm.filmaccio.data.models

import com.google.gson.annotations.SerializedName

// Data class che rappresenta i dettagli di una serie TV, leggi il commento alla classe Movie perché
// tanto sono praticamnete identiche.
// Qui abbiamo delle info in più però: le stagioni.
// Per ogni serie TV l'API ci dà una lista di stagioni (facendo una chiamata combinata con
// append_to_response) e per ogni stagione abbiamo una lista di episodi.
// I campi delle classi interne Season ed Episode sono abbastanza autoesplicativi. Ci servono
// per avere nella schermata di dettaglio tutte le informazioni sulla serie TV in questione.
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

    data class Season(
        @SerializedName("season_number") var number: Long,
        @SerializedName("air_date") var releaseDate: String?,
        @SerializedName("poster_path") var posterPath: String?,
        @SerializedName("overview") var overview: String,
        @SerializedName("episodes") var episodes: List<Episode> = emptyList(),
        @SerializedName("name") var name: String
    ) : java.io.Serializable

    data class Episode(
        @SerializedName("episode_number") val number: Long,
        @SerializedName("runtime") val duration: Int,
        @SerializedName("air_date") val releaseDate: String,
        @SerializedName("still_path") val imagePath: String?,
        @SerializedName("overview") var overview: String,
        @SerializedName("name") var name: String
    ) : java.io.Serializable
}