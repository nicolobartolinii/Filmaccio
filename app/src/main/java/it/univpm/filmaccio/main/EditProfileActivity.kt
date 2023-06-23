package it.univpm.filmaccio.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import it.univpm.filmaccio.R
import it.univpm.filmaccio.main.utils.FirestoreService

class EditProfileActivity : AppCompatActivity() {

    private lateinit var nameShownEditText: EditText
    private lateinit var nameShownTextInputLayout: TextInputLayout
    private lateinit var saveButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_edit)
        val nameShown = intent.getStringExtra("user")
        val uid = intent.getStringExtra("uid")
        Log.e("LogUser", nameShown!!)

        // Inizializza il TextView (sostituisci con l'ID del tuo TextView)
        nameShownEditText = findViewById(R.id.nomeVisualizzatoTextInputEditText)
        nameShownTextInputLayout = findViewById(R.id.nomeVisualizzatoTextInputLayout)
        nameShownEditText.setText(nameShown, TextView.BufferType.EDITABLE)

        saveButton = findViewById(R.id.buttonSaveChanges)

        saveButton.setOnClickListener {
            nameShownTextInputLayout.isErrorEnabled = false
            val newNameShown = nameShownEditText.text.toString()
            if (newNameShown.length > 50 || newNameShown.isEmpty() || newNameShown.length < 3) {
                nameShownTextInputLayout.isErrorEnabled = true
                nameShownTextInputLayout.error =
                    "Il nome visualizzato deve essere lungo tra 3 e 50 caratteri"
                return@setOnClickListener
            }

                FirestoreService.updateUserField(uid!!, "nameShown", newNameShown) { updateSuccessful ->
                    if (updateSuccessful) {
                        Toast.makeText(this, "Modifiche avvenute con successo", Toast.LENGTH_LONG).show()

                    } else {
                        Toast.makeText(this, "C'è stato un problema con le modifiche, riprova.", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }


