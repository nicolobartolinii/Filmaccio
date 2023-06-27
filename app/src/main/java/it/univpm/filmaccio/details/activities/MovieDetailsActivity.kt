package it.univpm.filmaccio.details.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.widget.ImageView
import android.widget.TextView
import android.widget.ViewFlipper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.imageview.ShapeableImageView
import it.univpm.filmaccio.R
import it.univpm.filmaccio.data.models.Director
import it.univpm.filmaccio.details.adapters.CastAdapter
import it.univpm.filmaccio.details.viewmodels.MovieDetailsViewModel
import it.univpm.filmaccio.details.viewmodels.MovieDetailsViewModelFactory

class MovieDetailsActivity : AppCompatActivity() {

    private lateinit var movieDetailsViewModel: MovieDetailsViewModel

    private lateinit var posterImage: ShapeableImageView
    private lateinit var backdropImage: ImageView
    private lateinit var titleTextView: TextView
    private lateinit var overviewTextView: TextView
    private lateinit var releaseDateTextView: TextView
    private lateinit var durationTextView: TextView
    private lateinit var directorTextView: TextView
    private lateinit var buttonWatched: MaterialButton
    private lateinit var buttonWatchlist: MaterialButton
    private lateinit var buttonFavorite: MaterialButton
    private lateinit var overviewFullText: String
    private lateinit var castRecyclerView: RecyclerView
    private lateinit var movieDirectors: List<Director>
    private lateinit var viewFlipperMovie: ViewFlipper
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_details)

        val movieId = intent.getLongExtra("movieId", 0L)
        movieDetailsViewModel = ViewModelProvider(
            this,
            MovieDetailsViewModelFactory(movieId)
        )[MovieDetailsViewModel::class.java]

        posterImage = findViewById(R.id.poster_image)
        backdropImage = findViewById(R.id.backdrop_image)
        titleTextView = findViewById(R.id.title_text_view)
        releaseDateTextView = findViewById(R.id.release_date_text_view)
        durationTextView = findViewById(R.id.duration_text_view)
        directorTextView = findViewById(R.id.director_text_view)
        buttonWatched = findViewById(R.id.button_watched)
        buttonWatchlist = findViewById(R.id.button_watchlist)
        buttonFavorite = findViewById(R.id.button_favorite)
        overviewTextView = findViewById(R.id.overview_text_view)
        castRecyclerView = findViewById(R.id.cast_recycler_view)
        viewFlipperMovie = findViewById(R.id.view_flipper_movie)

        viewFlipperMovie.displayedChild = 0

        castRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val typedValuePrimary = TypedValue()
        val typedValueTertiary = TypedValue()
        val theme = this.theme
        theme.resolveAttribute(
            com.google.android.material.R.attr.colorPrimary,
            typedValuePrimary,
            true
        )
        theme.resolveAttribute(
            com.google.android.material.R.attr.colorTertiary,
            typedValueTertiary,
            true
        )
        val color = typedValuePrimary.data
        val colorTertiary = typedValueTertiary.data
        val buttonColor = buttonWatched.backgroundTintList

        movieDetailsViewModel.currentMovie.observe(this) {
            it.credits.cast = it.credits.cast.take(50)
            it.credits.crew = it.credits.crew.filter { crewMember -> crewMember.job == "Director" }
            if (it.posterPath != null) Glide.with(this)
                .load("https://image.tmdb.org/t/p/w342${it.posterPath}")
                .into(posterImage)
            else posterImage.setImageResource(R.drawable.error_404)
            if (it.backdropPath != null) Glide.with(this)
                .load("https://image.tmdb.org/t/p/w780${it.backdropPath}")
                .into(backdropImage)
            else backdropImage.setImageResource(R.drawable.error_404)

            titleTextView.text = it.title
            overviewFullText = it.overview
            if (overviewFullText.isEmpty()) overviewTextView.text = "Non disponibile"
            else if (overviewFullText.length < 200) overviewTextView.text = overviewFullText
            else {
                val spannableString =
                    SpannableString(overviewFullText.take(200) + "... Più dettagli")
                spannableString.setSpan(
                    ForegroundColorSpan(color),
                    overviewFullText.take(200).length,
                    spannableString.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                overviewTextView.text = spannableString
            }
            if (it.releaseDate.isEmpty()) releaseDateTextView.text = "Diretto da:"
            else releaseDateTextView.text = "${it.releaseDate.substring(0, 4)} | Diretto da:"
            if (it.duration == 0) durationTextView.visibility = TextView.GONE
            else durationTextView.text = "${it.duration} min"
            movieDirectors = it.credits.crew
            directorTextView.text = movieDirectors.joinToString(", ") { director -> director.name }
            castRecyclerView.adapter = CastAdapter(it.credits.cast)

            titleTextView.setOnClickListener { _ ->
                MaterialAlertDialogBuilder(this)
                    .setTitle(it.title)
                    .setPositiveButton("Ok", null)
                    .show()
            }
            viewFlipperMovie.displayedChild = 1
        }

        overviewTextView.setOnClickListener {
            if (overviewFullText.length < 200) return@setOnClickListener
            if (overviewTextView.text.toString().length == 216) {
                // Se il testo è attualmente collassato, espandilo
                overviewTextView.text = overviewFullText
            } else {
                // Se il testo è attualmente espanso, collassalo
                val spannableString =
                    SpannableString(overviewFullText.take(200) + "... Più dettagli")
                spannableString.setSpan(
                    ForegroundColorSpan(color),
                    overviewFullText.take(200).length,
                    spannableString.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                overviewTextView.text = spannableString
            }
        }
        directorTextView.setOnClickListener {
            if (movieDirectors.size == 1) {
                val directorId = movieDirectors[0].id
                val intent = Intent(this, PersonDetailsActivity::class.java)
                intent.putExtra("personId", directorId)
                startActivity(intent)
            } else if (movieDirectors.size > 1) {
                MaterialAlertDialogBuilder(this)
                    .setTitle("Scegli un regista")
                    .setItems(movieDirectors.map { director -> director.name }
                        .toTypedArray()) { _, which ->
                        val directorId = movieDirectors[which].id
                        val intent = Intent(this, PersonDetailsActivity::class.java)
                        intent.putExtra("personId", directorId)
                        startActivity(intent)
                    }
                    .setNegativeButton("Annulla", null)
                    .show()
            }
        }

        movieDetailsViewModel.isMovieWatched.observe(this) { isWatched ->
            if (isWatched) {
                buttonWatched.setBackgroundColor(colorTertiary)
                buttonWatched.setIconResource(R.drawable.ic_check)
            } else {
                buttonWatched.setBackgroundColor(buttonColor!!.defaultColor)
                buttonWatched.setIconResource(R.drawable.round_remove_red_eye_24)
            }
        }

        movieDetailsViewModel.isMovieFavorited.observe(this) { isFavorited ->
            if (isFavorited) {
                buttonFavorite.setBackgroundColor(colorTertiary)
                buttonFavorite.setIconResource(R.drawable.ic_check)
            } else {
                buttonFavorite.setBackgroundColor(buttonColor!!.defaultColor)
                buttonFavorite.setIconResource(R.drawable.round_favorite_24)
            }
        }

        movieDetailsViewModel.isMovieInWatchlist.observe(this) { isInWatchlist ->
            if (isInWatchlist) {
                buttonWatchlist.setBackgroundColor(colorTertiary)
                buttonWatchlist.setIconResource(R.drawable.ic_check)
            } else {
                buttonWatchlist.setBackgroundColor(buttonColor!!.defaultColor)
                buttonWatchlist.setIconResource(R.drawable.round_more_time_24)
            }
        }

        buttonWatched.setOnClickListener {
            movieDetailsViewModel.toggleWatched(movieId)
        }

        buttonWatchlist.setOnClickListener {
            movieDetailsViewModel.toggleWatchlist(movieId)
        }

        buttonFavorite.setOnClickListener {
            movieDetailsViewModel.toggleFavorite(movieId)
        }
    }

}