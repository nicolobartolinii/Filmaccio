package it.univpm.filmaccio.details.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import it.univpm.filmaccio.R
import it.univpm.filmaccio.details.adapters.ProductsAdapter
import it.univpm.filmaccio.details.viewmodels.PersonDetailsViewModel
import it.univpm.filmaccio.details.viewmodels.PersonDetailsViewModelFactory

class PersonDetailsActivity : AppCompatActivity() {

    private lateinit var personDetailsViewModel: PersonDetailsViewModel

    private lateinit var personImage: ShapeableImageView
    private lateinit var personNameTextView: TextView
    private lateinit var knownForTextView: TextView
    private lateinit var genderTextView: TextView
    private lateinit var birthTextView: TextView
    private lateinit var deathLabelTextView: TextView
    private lateinit var deathTextView: TextView
    private lateinit var followButton: Button
    private lateinit var biographyTextView: TextView
    private lateinit var productsRecyclerView: RecyclerView
    private lateinit var biographyFullText: String

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_person_details)

        val personId = intent.getIntExtra("personId", 0)
        personDetailsViewModel = ViewModelProvider(
            this,
            PersonDetailsViewModelFactory(personId)
        )[PersonDetailsViewModel::class.java]

        personImage = findViewById(R.id.person_image)
        personNameTextView = findViewById(R.id.person_name)
        knownForTextView = findViewById(R.id.known_for)
        genderTextView = findViewById(R.id.gender)
        birthTextView = findViewById(R.id.birth)
        deathLabelTextView = findViewById(R.id.death_label)
        deathTextView = findViewById(R.id.death)
        followButton = findViewById(R.id.button_follow)
        biographyTextView = findViewById(R.id.biography_text_view)
        productsRecyclerView = findViewById(R.id.products_recycler_view)

        val typedValue = TypedValue()
        val theme = this.theme
        theme.resolveAttribute(com.google.android.material.R.attr.colorPrimary, typedValue, true)
        val color = typedValue.data

        personDetailsViewModel.currentPerson.observe(this) {
            if (it.profilePath != null) Glide.with(this)
                .load("https://image.tmdb.org/t/p/w342${it.profilePath}")
                .into(personImage)
            else personImage.setImageResource(R.drawable.error_404)

            personNameTextView.text = it.name
            knownForTextView.text = when (it.knownFor) {
                "Acting" -> "Recitazione"
                "Directing" -> "Regia"
                "Writing" -> "Scrittura"
                "Production" -> "Produzione"
                "Editing" -> "Montaggio"
                "Costume & Make-Up" -> "Costumi e Trucco"
                "Sound" -> "Suono"
                "Crew" -> "Cast Tecnico"
                "Visual Effects" -> "Effetti Visivi"
                "Camera" -> "Fotografia"
                "Lighting" -> "Illuminazione"
                "Creator" -> "Creatore"
                else -> "Non disponibile"
            }
            when (it.gender) {
                0 -> genderTextView.text = "Non specificato"
                1 -> genderTextView.text = "Femminile"
                2 -> genderTextView.text = "Maschile"
                else -> genderTextView.text = "Non disponibile"
            }
            if (it.birthday != null) {
                val year = it.birthday!!.substring(0, 4)
                val month = it.birthday!!.substring(5, 7)
                val day = it.birthday!!.substring(8, 10)
                if (it.placeOfBirth != null) birthTextView.text =
                    "$day/$month/$year (${it.placeOfBirth})"
                else birthTextView.text = "$day/$month/$year"
            } else {
                birthTextView.text = "Non disponibile"
            }
            if (it.deathday != null) {
                deathLabelTextView.visibility = TextView.VISIBLE
                deathTextView.visibility = TextView.VISIBLE
                val year = it.deathday!!.substring(0, 4)
                val month = it.deathday!!.substring(5, 7)
                val day = it.deathday!!.substring(8, 10)
                deathTextView.text = "$day/$month/$year"
            }
            biographyFullText = it.biography
            if (biographyFullText.isEmpty()) biographyTextView.text = "Non disponibile"
            else if (biographyFullText.length < 200) biographyTextView.text = biographyFullText
            else {
                val spannableString =
                    SpannableString(biographyFullText.take(200) + "... Più dettagli")
                spannableString.setSpan(
                    ForegroundColorSpan(color),
                    biographyFullText.take(200).length,
                    spannableString.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                biographyTextView.text = spannableString
            }
            productsRecyclerView.adapter = ProductsAdapter(it.products)
        }

        biographyTextView.setOnClickListener {
            if (biographyFullText.length < 200) return@setOnClickListener
            if (biographyTextView.text.toString().length == 216) {
                // Se il testo è attualmente collassato, espandilo
                biographyTextView.text = biographyFullText
            } else {
                // Se il testo è attualmente espanso, collassalo
                val spannableString =
                    SpannableString(biographyFullText.take(200) + "... Più dettagli")
                spannableString.setSpan(
                    ForegroundColorSpan(color),
                    biographyFullText.take(200).length,
                    spannableString.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                biographyTextView.text = spannableString
            }
        }
    }
}