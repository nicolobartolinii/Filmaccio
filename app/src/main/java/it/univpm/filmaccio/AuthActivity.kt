package it.univpm.filmaccio

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AuthActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        auth = Firebase.auth
    }

    public override fun onStart() {
        super.onStart()
        // Controlla se l'utente è già loggato
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val pass = 0 // TODO: passa alla schermata home direttamente
        }
    }
}