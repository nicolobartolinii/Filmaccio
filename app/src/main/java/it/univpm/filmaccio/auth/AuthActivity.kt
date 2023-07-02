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

// Questa è l'activity che viene avviata quando si apre l'app. Controlla se l'utente è già loggato e in caso positivo lo reindirizza alla MainActivity
class AuthActivity : AppCompatActivity() {

    // Variabile che conterrà l'istanza di firebaseAuth che si occupa di gestire l'autenticazione
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        // Inizializza l'istanza di firebaseAuth prendendola dalla classe UserUtils che si occupa di fornire utilità relative all'utente via FirebaseAuth
        auth = UserUtils.auth
    }

    // Questo metodo viene chiamato quando l'activity viene avviata
    public override fun onStart() {
        super.onStart()
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
                    baseContext, "Errore di lettura del database: $exception",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        // Se l'utente non è loggato non fa nulla e, data la struttura dell'XML di questa activity, mostra il fragment di login
    }
}