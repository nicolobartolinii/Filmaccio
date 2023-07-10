package it.univpm.filmaccio.data.models

/**
 * Data class che si occupa di contenere i dati principali di una lista dell'utente.
 * Questa classe viene usata nella schermata di profilo (nello specifico nella recycler view delle liste dell'utente).
 * Praticamente lì abbiamo delle MaterialCardView che vanno a rappresentare le liste dell'utente e questi rettangoli hanno
 * in alto il nome della lista e in basso tre immagini che rappresentano i primi tre film della lista.
 * Questi dati vengono memorizzati in questa classe.
 *
 * @param title titolo della lista
 * @param imageURL1 path dell'immagine del primo film della lista
 * @param imageURL2 path dell'immagine del secondo film della lista
 * @param imageURL3 path dell'immagine del terzo film della lista
 *
 * @author nicolobartolinii
 */
data class ProfileListItem(
    val title: String, val imageURL1: String?, val imageURL2: String?, val imageURL3: String?
)