package it.univpm.filmaccio

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import androidx.core.content.ContextCompat

@Suppress("DEPRECATION")
class AuthActivity : AppCompatActivity() {

    // private lateinit var entraTextView: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        /*entraTextView = findViewById(R.id.entraTextView)
        // Questa parte di codice rende la scritta di login di due colori differenti, evidenziando il nome dell'app
        val entraText = getString(R.string.entra_text)
        val spannableStringBuilder = SpannableStringBuilder(entraText)
        // Prendiamo il colore bianco personalizzato dai colori salvati nel file colors.xml
        val whiteTextColor = ForegroundColorSpan(ContextCompat.getColor(this, R.color.md))
        // Settiamo il colore per i primi 9 caratteri del testo sul bianco
        spannableStringBuilder.setSpan(whiteTextColor, 0, 8, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        // Prendiamo il colore primario personalizzato dai colori salvati nel file colors.xml
        val primaryTextColor = ForegroundColorSpan(ContextCompat.getColor(this, R.color.primary))
        // Settiamo il colore primario per gli ultimi caratteri (dal 10) del testo
        spannableStringBuilder.setSpan(primaryTextColor, 9, entraText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        // Settiamo il valore text della textView su quella a cui abbiamo modificato i colori
        entraTextView.text = spannableStringBuilder*/
    }
}