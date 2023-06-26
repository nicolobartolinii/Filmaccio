package it.univpm.filmaccio.main.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import it.univpm.filmaccio.R
import it.univpm.filmaccio.main.utils.Constants
import it.univpm.filmaccio.main.utils.FirestoreService
import it.univpm.filmaccio.main.utils.UserUtils

class EditProfileActivity : AppCompatActivity() {

    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    private lateinit var buttonBack: Button
    private lateinit var nameShownEditText: EditText
    private lateinit var nameShownTextInputLayout: TextInputLayout
    private lateinit var saveNameButton: Button
    private lateinit var savePropicButton: Button
    private lateinit var saveBackdropButton: Button
    private lateinit var buttonChangePassword: Button
    private lateinit var propicImageView: ShapeableImageView
    private lateinit var backdropImageView: ShapeableImageView

    private lateinit var callback: OnBackPressedCallback

    private var selectedBackdropImageUrl: String? = Constants.DESERT_BACKDROP_URL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_edit)

        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@EditProfileActivity, MainActivity::class.java)
                intent.putExtra("reloadProfile", true)
                startActivity(intent)
                finish()
            }
        }

        onBackPressedDispatcher.addCallback(this, callback)

        val nameShown = intent.getStringExtra("nameShown")
        val uid = intent.getStringExtra("uid")
        val propic = intent.getStringExtra("propic")
        val backdropImage = intent.getStringExtra("backdrop")

        buttonBack = findViewById(R.id.buttonBack)
        nameShownEditText = findViewById(R.id.nomeVisualizzatoTextInputEditText)
        nameShownTextInputLayout = findViewById(R.id.nomeVisualizzatoTextInputLayout)
        saveNameButton = findViewById(R.id.buttonSaveName)
        savePropicButton = findViewById(R.id.button_save_propic)
        saveBackdropButton = findViewById(R.id.button_save_backdrop)
        buttonChangePassword = findViewById(R.id.buttonChangePassword)
        propicImageView = findViewById(R.id.propic_change_simage_view)
        backdropImageView = findViewById(R.id.backdrop_change_simage_view)

        nameShownEditText.setText(nameShown, TextView.BufferType.EDITABLE)

        Glide.with(this).load(propic).into(propicImageView)
        Glide.with(this).load(backdropImage).into(backdropImageView)

        buttonBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("reloadProfile", true)
            startActivity(intent)
            finish()
        }

        saveNameButton.setOnClickListener {
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
                    val snackbar = Snackbar.make(
                        buttonChangePassword,
                        "Il tuo nuovo nome visualizzato è $newNameShown",
                        Snackbar.LENGTH_LONG
                    )
                    snackbar.animationMode = Snackbar.ANIMATION_MODE_SLIDE
                    snackbar.setAction("Indietro") {
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("reloadProfile", true)
                        startActivity(intent)
                        finish()
                    }
                    snackbar.show()

                } else {
                    Toast.makeText(
                        this, "C'è stato un problema con la modifica, riprova.", Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
        val auth = UserUtils.auth

        // funzione per cambiare la password, prendo da email la mail di ogni utente e chiamo metodo predefinito di firebase
        buttonChangePassword.setOnClickListener {
            val email = intent.getStringExtra("email")
            auth.sendPasswordResetEmail(email!!).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val snackbar = Snackbar.make(
                        buttonChangePassword, "Email inviata", Snackbar.LENGTH_LONG
                    )
                    snackbar.animationMode = Snackbar.ANIMATION_MODE_SLIDE
                    snackbar.setAction("Indietro") {
                        finish()
                    }
                    snackbar.show()
                }
            }
        }

        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    // ottieni l'URL dell'immagine di sfondo selezionato
                    selectedBackdropImageUrl = result.data?.getStringExtra("selectedImageUrl")
                    // carica l'immagine nella tua ImageView
                    Glide.with(this).load(selectedBackdropImageUrl).into(backdropImageView)

                    saveBackdropButton.isEnabled = true
                }
            }

        backdropImageView.setOnClickListener {
            val intent = Intent(this, ChangeBackdropActivity::class.java)
            resultLauncher.launch(intent)
        }

        saveBackdropButton.setOnClickListener {
            FirestoreService.updateUserField(
                uid!!,
                "backdropImage",
                selectedBackdropImageUrl!!
            ) { updateSuccessful ->
                if (updateSuccessful) {
                    val snackbar = Snackbar.make(
                        buttonChangePassword,
                        "Nuova immagine di sfondo impostata",
                        Snackbar.LENGTH_LONG
                    )
                    snackbar.animationMode = Snackbar.ANIMATION_MODE_SLIDE
                    snackbar.setAction("Indietro") {
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("reloadProfile", true)
                        startActivity(intent)
                        finish()
                    }
                    snackbar.show()
                } else {
                    Toast.makeText(
                        this, "C'è stato un problema con la modifica, riprova.", Toast.LENGTH_LONG
                    ).show()

                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        callback.remove()
    }
}