package it.univpm.filmaccio.main

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
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
            //nomeVisualizzatoTectInputLayout.isErrorEnabled = false
            var newNameShown = usernameTextView.text.toString()
            if (nameShown.length > 50 || nameShown.isEmpty() || nameShown.length < 3) {
                usernameTextView.isErrorEnabled = true
                usernameTextView.error = "Il nome visualizzato deve essere lungo tra 3 e 50 caratteri"
                return@setOnClickListener
            }
            val updateSuccesful = FirestoreService.updateUserField(currentUser.uid, "nameShown", newNameShown)
            if (updateSuccesful) {
                Toast.makeText(this, "Modifiche avvenute con successo", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "C'è stato un problema con le modifiche, riprova.", Toast.LENGTH_LONG).show()
                }
        }
}
