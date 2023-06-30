package it.univpm.filmaccio.data.models

import com.google.gson.annotations.SerializedName

// Data class che rappresenta una persona (con persona intendiamo tutte le persone contenute nel database
// di TMDB, quindi attori, registi, scrittori, make-up artist, VFX artist ecc...).
// È difficile che un utente vada a finire nella schermata di dettaglio di una persona che non sia un attore,
// un regista o uno scrittore, ma è comunque possibile. Quindi
// questa classe rappresenta una persona generica, che può essere un attore, un regista, uno scrittore o qualsiasi altra cosa.
// Per ogni persona memorizziamo: id, nome, percorso TMDB della sua foto, biografia, data di nascita,
// eventuale data di morte, luogo di nascita, genere, cosa è famoso per (attore, regista, ecc...),
// eventuale luogo di morte e i crediti (cast e crew).
// Innanzitutto noterete che alcuni campi sono nullable, questo perché non tutti i campi sono presenti
// per ogni persona, ad esempio non tutti hanno una biografia, non tutti hanno una data di morte, non
// tutti hanno un luogo di morte ecc...
// Questo sia perché ovviamente alcuni sono ancora vivi, sia perché l'API non ha tutte le informazioni
// di tutti, ad esempio un tizio sconosciuto tipo Giangilberto Esmeraldini potrebbe tranquillamente
// non avere informazioni su nessuno dei campi tranne il nome.
// Quindi rendendo i campi nullable ci assicuriamo di poter gestire tutti i casi possibili (Ovviamente
// credo ci siano delle situazioni estreme che non ho gestito alla perfezione poi nella schermata di dettaglio,
// perché ci sono talmente tante possibilità che sicuramente mi è sfuggito qualcosa, ma per
// la maggior parte dei casi dovrebbe funzionare).
// Inoltre i combined_credits che vediamo quì rappresentano i crediti combinati di ogni persona
// (combinati perché FILM+SERIE TV), quindi per ogni persona avremo dei combinedCredits contenenti una
// lista di cast e una lista di crew. La lista di cast sarà la lista dei prodotti (film E serie TV)
// in cui la persona ha RECITATO, mentre la lista di crew sarà la lista dei prodotti (film E serie TV)
// in cui la persona ha LAVORATO non come attore (quindi non solo come regista, ma anche come sceneggiatore, produttore, ecc...).
// Poi c'è un attributo products che non fa parte della risposta dell'API, ma che noi aggiungiamo per
// poter mostrare la lista dei prodotti relativi alla persona. Questo attributo è una lista
// di products che viene riempita a runtime.
// La data class interna Product è una classe che rappresenta un prodotto (film o serie TV) relativo
// alla persona. Per ogni prodotto memorizziamo: id, titolo, percorso TMDB del poster,
// tipologia di prodotto (film O serie TV) e popolarità.
// La popolarità è un valore che ci serve per poter ordinare i prodotti in base alla popolarità,
// quindi in base a quanto sono popolari. Questo valore è un valore che ci viene
// fornito dall'API, quindi non è un valore che noi calcoliamo.
// Il mediaType è un valore che ci serve per poter distinguere i film dalle serie TV, quindi se il
// mediaType è "movie" allora il prodotto è un film, se invece il mediaType è "tv" allora il prodotto
// è una serie TV. Questo ci consente poi di avviare l'activity di dettagio giusta cliccando sul film o sulla serie TV.
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

    data class CombinedCredits(
        @SerializedName("cast") val cast: List<Product>,
        @SerializedName("crew") val crew: List<Product>
    )

    data class Product(
        @SerializedName("id") val id: Long,
        @SerializedName("title", alternate = ["name"]) val title: String,
        @SerializedName("poster_path") val posterPath: String?,
        @SerializedName("media_type") val mediaType: String,
        @SerializedName("popularity") val popularity: Double,
    )
}