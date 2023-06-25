package it.univpm.filmaccio.main

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import it.univpm.filmaccio.R
import it.univpm.filmaccio.main.utils.FirestoreService
import it.univpm.filmaccio.main.utils.UserUtils

class EditProfileActivity : AppCompatActivity() {

    private lateinit var nameShownEditText: EditText
    private lateinit var nameShownTextInputLayout: TextInputLayout
    private lateinit var saveButton: Button
    private lateinit var buttonChangePassword : Button
    private lateinit var propicImageView: ShapeableImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_edit)
        val nameShown = intent.getStringExtra("nameShown")
        val uid = intent.getStringExtra("uid")
        val propic = intent.getStringExtra("propic")

        // Inizializza il TextView (sostituisci con l'ID del tuo TextView)
        nameShownEditText = findViewById(R.id.nomeVisualizzatoTextInputEditText)
        nameShownTextInputLayout = findViewById(R.id.nomeVisualizzatoTextInputLayout)
        buttonChangePassword =  findViewById(R.id.buttonChangePassword)
        saveButton = findViewById(R.id.buttonSaveChanges)
        propicImageView = findViewById(R.id.propicSetImageView)

        nameShownEditText.setText(nameShown, TextView.BufferType.EDITABLE)

        Glide.with(this)
            .load(propic)
            .into(propicImageView)

        saveButton.setOnClickListener {
            nameShownTextInputLayout.isErrorEnabled = false
            val newNameShown = nameShownEditText.text.toString()
            if (newNameShown.length > 50 || newNameShown.isEmpty() || newNameShown.length < 3) {
                nameShownTextInputLayout.isErrorEnabled = true
                nameShownTextInputLayout.error =
                    "Il nome visualizzato deve essere lungo tra 3 e 50 caratteri"
                return@setOnClickListener
            }
// richiama la funzione chew fa un update del nome utente
                FirestoreService.updateUserField(uid!!, "nameShown", newNameShown) { updateSuccessful ->
                    if (updateSuccessful) {
                        Toast.makeText(this, "Modifiche avvenute con successo", Toast.LENGTH_LONG).show()

                    } else {
                        Toast.makeText(this, "C'è stato un problema con le modifiche, riprova.", Toast.LENGTH_LONG).show()
                    }
                }
            }
       val auth= UserUtils.auth
// funzione per cambiare la password, prendo da email la mail di ogni utente e chiamo metodo predefinito di firebase
        buttonChangePassword.setOnClickListener {
            val email = intent.getStringExtra("email")
            auth.sendPasswordResetEmail(email!!)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val snackbar = Snackbar.make(buttonChangePassword, "Email inviata", Snackbar.LENGTH_LONG)
                        snackbar.setAction("Indietro") {
                            // codice per tornare indietro
                        }
                        snackbar.show()
                        Log.d(TAG, "Email sent.")
                        snackbar.setAction("Indietro") {
                            finish()
                        }
                    }
                }
        }
        }
    }


