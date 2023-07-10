package it.univpm.filmaccio.data.models

import com.google.gson.annotations.SerializedName

/**
 * Data class che rappresenta una persona (con persona intendiamo tutte le persone contenute nel database
 * di TMDB, quindi attori, registi, scrittori, make-up artist, VFX artist ecc...).
 * È difficile che un utente vada a finire nella schermata di dettaglio di una persona che non sia un attore,
 * un regista o uno scrittore, ma è comunque possibile attraverso la ricerca. Quindi
 * questa classe rappresenta una persona generica, che può essere un attore, un regista, uno scrittore o qualsiasi altra cosa.
 * C'è una proprietà products che non fa parte della risposta dell'API, ma che aggiungiamo per
 * poter mostrare la lista dei prodotti relativi alla persona. Questa proprietà è una lista
 * di products che viene riempita a runtime.
 *
 * @param id id della persona
 * @param name nome della persona
 * @param profilePath percorso TMDB della foto della persona
 * @param biography biografia della persona
 * @param birthday data di nascita della persona
 * @param deathday eventuale data di morte della persona
 * @param placeOfBirth luogo di nascita della persona
 * @param gender genere della persona (0 = non specificato, 1 = donna, 2 = uomo)
 * @param knownFor per cosa è conosciuto (recitazione, regia, ecc...)
 * @param placeOfDeath eventuale luogo di morte della persona
 * @param combinedCredits crediti combinati della persona (film + serie TV)
 * @property products lista dei prodotti relativi alla persona
 *
 * @author nicolobartolinii
 */
data class Person(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("profile_path") val profilePath: String?,
    @SerializedName("biography") var biography: String,
    @SerializedName("birthday") var birthday: String?,
    @SerializedName("deathday") var deathday: String?,
    @SerializedName("place_of_birth") val placeOfBirth: String?,
    @SerializedName("gender") var gender: Int,
    @SerializedName("known_for_department") var knownFor: String,
    @SerializedName("place_of_death") val placeOfDeath: String?,
    @SerializedName("combined_credits") var combinedCredits: CombinedCredits
) {
    var products: List<Product> = listOf()

    /**
     * Data class che rappresenta i crediti combinati di una persona (combinati perché FILM+SERIE TV).
     * Per ogni persona avremo dei combinedCredits contenenti una lista di cast e una lista di crew.
     * La lista di cast sarà la lista dei prodotti (film E serie TV) in cui la persona ha RECITATO,
     * mentre la lista di crew sarà la lista dei prodotti (film E serie TV) in cui la persona ha
     * LAVORATO non come attore (quindi non solo come regista, ma anche come sceneggiatore, produttore, ecc...).
     *
     * @param cast lista dei prodotti (film E serie TV) in cui la persona ha RECITATO
     * @param crew lista dei prodotti (film E serie TV) in cui la persona ha LAVORATO non come attore
     *
     * @author nicolobartolinii
     */
    data class CombinedCredits(
        @SerializedName("cast") val cast: List<Product>,
        @SerializedName("crew") val crew: List<Product>
    )

    /**
     * Data class che rappresenta un prodotto (film O serie TV) relativo alla persona.
     * La popolarità è un valore che ci serve per poter ordinare i prodotti in base alla popolarità,
     * quindi in base a quanto sono popolari. Questo valore è un valore che ci viene
     * fornito dall'API, quindi non è un valore che noi calcoliamo.
     * Il mediaType è un valore che ci serve per poter distinguere i film dalle serie TV, quindi se il
     * mediaType è "movie" allora il prodotto è un film, se invece il mediaType è "tv" allora il prodotto
     * è una serie TV. Questo ci consente poi di avviare l'activity di dettagio giusta cliccando sul film o sulla serie TV.
     *
     * @param id id del prodotto
     * @param title titolo del prodotto
     * @param posterPath percorso TMDB del poster del prodotto
     * @param mediaType tipologia di prodotto (film O serie TV)
     * @param popularity popolarità del prodotto
     *
     * @author nicolobartoliniii
     */
    data class Product(
        @SerializedName("id") val id: Long,
        @SerializedName("title", alternate = ["name"]) val title: String,
        @SerializedName("poster_path") val posterPath: String?,
        @SerializedName("media_type") val mediaType: String,
        @SerializedName("popularity") val popularity: Double,
    )
}