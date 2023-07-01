package it.univpm.filmaccio.details.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import android.widget.ViewFlipper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Timestamp
import it.univpm.filmaccio.R
import it.univpm.filmaccio.data.models.Director
import it.univpm.filmaccio.details.adapters.CastAdapter
import it.univpm.filmaccio.details.viewmodels.MovieDetailsViewModel
import it.univpm.filmaccio.details.viewmodels.MovieDetailsViewModelFactory
import it.univpm.filmaccio.main.activities.ViewAllActivity
import it.univpm.filmaccio.main.utils.UserUtils
import java.text.SimpleDateFormat
import java.util.Locale

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
    private lateinit var movieRatingBar: RatingBar
    private lateinit var submitRatingButton: Button
    private lateinit var reviewInputLayout: TextInputLayout
    private lateinit var reviewEditText: TextInputEditText
    private lateinit var editReviewButton: Button
    private lateinit var submitReviewButton: Button
    private lateinit var averageRatingBar: RatingBar
    private lateinit var buttonViewAllReviews: Button

    private var loadingOperations = 0

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_details)

        val movieId = intent.getLongExtra("movieId", 0L)
        movieDetailsViewModel = ViewModelProvider(
            this, MovieDetailsViewModelFactory(movieId)
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
        movieRatingBar = findViewById(R.id.movie_rating_bar)
        submitRatingButton = findViewById(R.id.submit_rating_button)
        reviewInputLayout = findViewById(R.id.review_text_input_layout)
        reviewEditText = findViewById(R.id.review_text_input_edit_text)
        editReviewButton = findViewById(R.id.edit_review_button)
        submitReviewButton = findViewById(R.id.submit_review_button)
        averageRatingBar = findViewById(R.id.average_rating_bar)
        buttonViewAllReviews = findViewById(R.id.button_view_all_reviews)

        viewFlipperMovie.displayedChild = 0

        castRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val typedValuePrimary = TypedValue()
        val typedValueTertiary = TypedValue()
        val theme = this.theme
        theme.resolveAttribute(
            com.google.android.material.R.attr.colorPrimary, typedValuePrimary, true
        )
        theme.resolveAttribute(
            com.google.android.material.R.attr.colorTertiary, typedValueTertiary, true
        )
        val color = typedValuePrimary.data
        val colorTertiary = typedValueTertiary.data
        val buttonColor = buttonWatched.backgroundTintList

        movieDetailsViewModel.currentMovie.observe(this) {
            it.credits.cast = it.credits.cast.take(50)
            it.credits.crew = it.credits.crew.filter { crewMember -> crewMember.job == "Director" }
            if (it.posterPath != null) Glide.with(this)
                .load("https://image.tmdb.org/t/p/w342${it.posterPath}").into(posterImage)
            else posterImage.setImageResource(R.drawable.error_404)
            if (it.backdropPath != null) Glide.with(this)
                .load("https://image.tmdb.org/t/p/w780${it.backdropPath}").into(backdropImage)
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
                MaterialAlertDialogBuilder(this).setTitle(it.title).setPositiveButton("Ok", null)
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
                MaterialAlertDialogBuilder(this).setTitle("Scegli un regista")
                    .setItems(movieDirectors.map { director -> director.name }
                        .toTypedArray()) { _, which ->
                        val directorId = movieDirectors[which].id
                        val intent = Intent(this, PersonDetailsActivity::class.java)
                        intent.putExtra("personId", directorId)
                        startActivity(intent)
                    }.setNegativeButton("Annulla", null).show()
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

        movieDetailsViewModel.isMovieRated.observe(this) { isRated ->
            if (isRated) {
                movieDetailsViewModel.loadCurrentMovieRating(
                    UserUtils.getCurrentUserUid()!!, movieId
                )
            }
        }

        movieDetailsViewModel.isMovieReviewed.observe(this) { isReviewed ->
            if (isReviewed) {
                movieDetailsViewModel.loadCurrentMovieReview(
                    UserUtils.getCurrentUserUid()!!, movieId
                )
            }
        }

        movieDetailsViewModel.currentMovieRating.observe(this) { movieRating ->
            if (movieRating != null) {
                movieRatingBar.rating = movieRating.first
                submitRatingButton.isEnabled = false
                movieRatingBar.setOnRatingBarChangeListener { _, rating, _ ->
                    submitRatingButton.isEnabled = movieRatingBar.rating != movieRating.first
                    submitRatingButton.setOnClickListener {
                        movieDetailsViewModel.updateMovieRating(
                            UserUtils.getCurrentUserUid()!!, movieId, rating, Timestamp.now()
                        )
                        Toast.makeText(
                            this@MovieDetailsActivity, "Valutazione aggiornata", Toast.LENGTH_SHORT
                        ).show()
                        submitRatingButton.isEnabled = false
                    }
                }
            } else {
                submitRatingButton.isEnabled = false
                movieRatingBar.setOnRatingBarChangeListener { _, rating, _ ->
                    submitRatingButton.isEnabled = true
                    submitRatingButton.setOnClickListener {
                        movieDetailsViewModel.updateMovieRating(
                            UserUtils.getCurrentUserUid()!!, movieId, rating, Timestamp.now()
                        )
                        Toast.makeText(
                            this@MovieDetailsActivity, "Valutazione aggiunta", Toast.LENGTH_SHORT
                        ).show()
                        submitRatingButton.isEnabled = false
                    }
                }
            }
        }

        movieDetailsViewModel.currentMovieReview.observe(this) { movieReview ->
            if (movieReview != null) {
                editReviewButton.isEnabled = true
                submitReviewButton.isEnabled = false
                reviewEditText.setText(movieReview.first)
                reviewEditText.isEnabled = false
                val date = movieReview.second.toDate()
                val format = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ITALY)
                val dateString = format.format(date)
                reviewInputLayout.helperText = "Ultima modifica: $dateString"
                editReviewButton.setOnClickListener {
                    editReviewButton.isEnabled = false
                    submitReviewButton.isEnabled = true
                    reviewEditText.isEnabled = true
                }
                submitReviewButton.setOnClickListener {
                    reviewInputLayout.isErrorEnabled = false
                    val reviewText = reviewEditText.text.toString()
                    if (reviewText.length < 5) {
                        reviewInputLayout.isErrorEnabled = true
                        reviewInputLayout.error =
                            "La recensione deve essere lunga almeno 5 caratteri"
                        return@setOnClickListener
                    }
                    if (reviewText.contains("http") || reviewText.contains("www")) {
                        reviewInputLayout.isErrorEnabled = true
                        reviewInputLayout.error = "La recensione non può contenere link"
                        return@setOnClickListener
                    }
                    if (reviewText == movieReview.first) {
                        reviewInputLayout.isErrorEnabled = true
                        reviewInputLayout.error = "La recensione è identica a quella precedente"
                        return@setOnClickListener
                    }
                    val timestamp = Timestamp.now()
                    movieDetailsViewModel.updateMovieReview(
                        UserUtils.getCurrentUserUid()!!, movieId, reviewText, timestamp
                    )
                    Toast.makeText(
                        this@MovieDetailsActivity, "Recensione aggiornata", Toast.LENGTH_SHORT
                    ).show()
                    submitReviewButton.isEnabled = false
                    editReviewButton.isEnabled = true
                    reviewEditText.isEnabled = false
                    reviewInputLayout.helperText =
                        "Ultima modifica: ${format.format(timestamp.toDate())}"
                }
            } else {
                editReviewButton.isEnabled = false
                submitReviewButton.isEnabled = true
                submitReviewButton.setOnClickListener {
                    reviewInputLayout.isErrorEnabled = false
                    val reviewText = reviewEditText.text.toString()
                    if (reviewText.length < 5) {
                        reviewInputLayout.isErrorEnabled = true
                        reviewInputLayout.error =
                            "La recensione deve essere lunga almeno 5 caratteri"
                        return@setOnClickListener
                    }
                    if (reviewText.contains("http") || reviewText.contains("www")) {
                        reviewInputLayout.isErrorEnabled = true
                        reviewInputLayout.error = "La recensione non può contenere link"
                        return@setOnClickListener
                    }
                    val timestamp = Timestamp.now()
                    movieDetailsViewModel.updateMovieReview(
                        UserUtils.getCurrentUserUid()!!, movieId, reviewText, timestamp
                    )
                    val format = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ITALY)
                    Toast.makeText(
                        this@MovieDetailsActivity, "Recensione aggiunta", Toast.LENGTH_SHORT
                    ).show()
                    submitReviewButton.isEnabled = false
                    editReviewButton.isEnabled = true
                    reviewEditText.isEnabled = false
                    reviewInputLayout.helperText =
                        "Ultima modifica: ${format.format(timestamp.toDate())}"
                }
            }
        }

        movieDetailsViewModel.averageMovieRating.observe(this) {
            averageRatingBar.rating = it
        }

        movieDetailsViewModel.movieReviews.observe(this) {
            if (it.isEmpty()) {
                buttonViewAllReviews.visibility = View.INVISIBLE
            } else {
                buttonViewAllReviews.visibility = View.VISIBLE
                buttonViewAllReviews.setOnClickListener { _ ->
                    val intent = Intent(this, ViewAllActivity::class.java)
                    intent.putExtra("entities", ArrayList(it))
                    intent.putExtra("title", "Recensioni")
                    startActivity(intent)
                }
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