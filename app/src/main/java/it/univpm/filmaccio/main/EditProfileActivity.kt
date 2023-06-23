package it.univpm.filmaccio.main

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import it.univpm.filmaccio.R

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference

class EditProfileActivity : AppCompatActivity() {

    private lateinit var usernameTextView: TextView
    private lateinit var saveButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_edit)
        val nameShown = intent.getStringExtra("user")
        Log.e("LogUser", nameShown!!)

        // Inizializza il TextView (sostituisci con l'ID del tuo TextView)
        usernameTextView = findViewById(R.id.nomeVisualizzatoTextInputEditText)
        usernameTextView.setText(nameShown, TextView.BufferType.EDITABLE)

        saveButton = findViewById(R.id.buttonSaveChanges)
        saveButton.setOnClickListener {
            // Ottieni il testo inserito dall'utente
            val newUsername = usernameTextView.text.toString()

            // Ottieni un'istanza del database Firebase
            val database = FirebaseDatabase.getInstance()

            // Ottieni un riferimento al percorso del tuo nodo utente nel database
            val userRef: DatabaseReference = database.getReference("utenti")

            // Salva il nuovo username nel database
            val userId = "id_utente"  // Specifica l'ID dell'utente appropriato
            userRef.child(userId).child("username").setValue(newUsername)
                .addOnSuccessListener {
                    // Salvataggio completato con successo
                    Log.e("LogSave", "Nuovo username salvato nel database: $newUsername")
                }
                .addOnFailureListener { error ->
                    // Errore durante il salvataggio
                    Log.e("LogSave", "Errore durante il salvataggio del nuovo username: ${error.message}")
                }
        }

    }
}
