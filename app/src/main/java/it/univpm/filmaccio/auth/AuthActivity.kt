package it.univpm.filmaccio.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import it.univpm.filmaccio.R
import it.univpm.filmaccio.main.activities.MainActivity
import it.univpm.filmaccio.main.utils.FirestoreService
import it.univpm.filmaccio.main.utils.UserUtils

/**
 * Questa è l'Activity entry point dell'app. Controlla se l'utente è già loggato
 * e, in caso positivo, lo reindirizza alla MainActivity.
 *
 * <p>L'Activity utilizza la classe UserUtils per ottenere un'istanza di FirebaseAuth e, attraverso
 * questa, controllare se l'utente è già loggato.
 *
 * <p>Se l'utente non è loggato, verrà visualizzato il fragment di login.
 *
 * @see it.univpm.filmaccio.auth.fragments.LoginFragment
 *
 * @author nicolobartolinii
 */
class AuthActivity : AppCompatActivity() {

    // Variabile che conterrà l'istanza di FirebaseAuth che si occupa di gestire l'autenticazione
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inizializza l'istanza di firebaseAuth prendendola dalla classe UserUtils che si occupa di fornire utilità relative all'utente via FirebaseAuth
        auth = UserUtils.auth
        setContentView(R.layout.loading_screen)
        // Controlla se l'utente è già loggato
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // Controlla se l'utente esiste in Firestore
            val docRef = FirestoreService.collectionUsers.document(currentUser.uid)
            docRef.get().addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    // Se l'utente esiste sia in Auth che in Firestore, lo reindirizza alla MainActivity
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // Se l'utente non esiste in Firestore, gestisci di conseguenza (potrebbe essere necessario il logout)
                    FirebaseAuth.getInstance().signOut()
                }
            }.addOnFailureListener { exception ->
                // Gestisci l'errore di lettura da Firestore
                Toast.makeText(
                    baseContext, "Errore di lettura del database: $exception", Toast.LENGTH_SHORT
                ).show()
                setContentView(R.layout.activity_auth)
            }
        } else setContentView(R.layout.activity_auth)
    }
}