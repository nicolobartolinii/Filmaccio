package it.univpm.filmaccio.details

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import it.univpm.filmaccio.R

class MovieDetailsActivity : AppCompatActivity() {

    private lateinit var movieDetailsViewModel: MovieDetailsViewModel

    private lateinit var posterImage: ShapeableImageView
    private lateinit var backdropImage: ImageView
    private lateinit var titleTextView: TextView
    private lateinit var overviewTextView: TextView
    private lateinit var releaseDateTextView: TextView
    private lateinit var durationTextView: TextView
    private lateinit var directorTextView: TextView
    private lateinit var overviewFullText: String
    private lateinit var castRecyclerView: RecyclerView
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_details)

        val movieId = intent.getIntExtra("movieId", 0)
        movieDetailsViewModel = ViewModelProvider(this, MovieDetailsViewModelFactory(movieId))[MovieDetailsViewModel::class.java]

        posterImage = findViewById(R.id.poster_image)
        backdropImage = findViewById(R.id.backdrop_image)
        titleTextView = findViewById(R.id.title_text_view)
        overviewTextView = findViewById(R.id.overview_text_view)
        releaseDateTextView = findViewById(R.id.release_date_text_view)
        durationTextView = findViewById(R.id.duration_text_view)
        directorTextView = findViewById(R.id.director_text_view)
        castRecyclerView = findViewById(R.id.cast_recycler_view)

        castRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val typedValue = TypedValue()
        val theme = this.theme
        theme.resolveAttribute(com.google.android.material.R.attr.colorPrimary, typedValue, true)
        val color = typedValue.data

        movieDetailsViewModel.currentMovie.observe(this) {
            it.credits.cast = it.credits.cast.take(50)
            it.credits.crew = it.credits.crew.filter { crewMember -> crewMember.job == "Director" }
            Glide.with(this)
                .load("https://image.tmdb.org/t/p/w185${it.posterPath}")
                .into(posterImage)

            Glide.with(this)
                .load("https://image.tmdb.org/t/p/w500${it.backdropPath}")
                .into(backdropImage)

            titleTextView.text = it.title
            overviewFullText = it.overview
            if (overviewFullText.length < 200) overviewTextView.text = overviewFullText
            else {
                val spannableString = SpannableString(overviewFullText.take(200) + "... Più dettagli")
                spannableString.setSpan(
                    ForegroundColorSpan(color),
                    overviewFullText.take(200).length,
                    spannableString.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                overviewTextView.text = spannableString
            }
            releaseDateTextView.text = "${it.releaseDate.substring(0, 4)} | Diretto da:"
            durationTextView.text = "${it.duration} min"
            directorTextView.text = it.credits.crew.joinToString(", ") { director -> director.name }
            castRecyclerView.adapter = CastAdapter(it.credits.cast)
        }

        overviewTextView.setOnClickListener {
            if (overviewFullText.length < 200) return@setOnClickListener
            if (overviewTextView.text.toString().length == 216) {
                // Se il testo è attualmente collassato, espandilo
                overviewTextView.text = overviewFullText
            } else {
                // Se il testo è attualmente espanso, collassalo
                val spannableString = SpannableString(overviewFullText.take(200) + "... Più dettagli")
                spannableString.setSpan(
                    ForegroundColorSpan(color),
                    overviewFullText.take(200).length,
                    spannableString.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                overviewTextView.text = spannableString
            }
        }
    }
}