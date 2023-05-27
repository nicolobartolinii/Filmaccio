package it.univpm.filmaccio.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import it.univpm.filmaccio.R
import it.univpm.filmaccio.auth.AuthActivity

class HomeActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var textView: TextView
    private lateinit var buttonLogout: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        auth = FirebaseAuth.getInstance()

        textView = findViewById(R.id.textView2)
        buttonLogout = findViewById(R.id.button2)

        textView.text = "Utente ${auth.currentUser?.email} loggato"

        buttonLogout.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}