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
import it.univpm.filmaccio.details.adapters.SeasonsAdapter
import it.univpm.filmaccio.details.viewmodels.SeriesDetailsViewModel
import it.univpm.filmaccio.details.viewmodels.SeriesDetailsViewModelFactory
import it.univpm.filmaccio.main.activities.ViewAllActivity
import it.univpm.filmaccio.main.utils.FirestoreService
import it.univpm.filmaccio.main.utils.UserUtils
import java.text.SimpleDateFormat
import java.util.Locale

class SeriesDetailsActivity : AppCompatActivity() {

    private lateinit var seriesDetailsViewModel: SeriesDetailsViewModel

    private lateinit var posterImage: ShapeableImageView
    private lateinit var backdropImage: ImageView
    private lateinit var titleTextView: TextView
    private lateinit var overviewTextView: TextView
    private lateinit var releaseDateTextView: TextView
    private lateinit var durationTextView: TextView
    private lateinit var directorTextView: TextView
    private lateinit var buttonWatching: MaterialButton
    private lateinit var buttonWatchlist: MaterialButton
    private lateinit var buttonFavorite: MaterialButton
    private lateinit var watchingTextView: TextView
    private lateinit var overviewFullText: String
    private lateinit var seasonsRecyclerView: RecyclerView
    private lateinit var castRecyclerView: RecyclerView
    private lateinit var seriesDirectors: List<Director>
    private lateinit var viewFlipperSeries: ViewFlipper
    private lateinit var seriesRatingBar: RatingBar
    private lateinit var submitRatingButton: Button
    private lateinit var reviewInputLayout: TextInputLayout
    private lateinit var reviewEditText: TextInputEditText
    private lateinit var editReviewButton: Button
    private lateinit var submitReviewButton: Button
    private lateinit var averageRatingBar: RatingBar
    private lateinit var buttonViewAllReviews: Button

    private var isSeriesInWatching = false

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_series_details)

        val seriesId = intent.getLongExtra("seriesId", 0L)
        seriesDetailsViewModel = ViewModelProvider(
            this, SeriesDetailsViewModelFactory(seriesId)
        )[SeriesDetailsViewModel::class.java]

        posterImage = findViewById(R.id.poster_image)
        backdropImage = findViewById(R.id.backdrop_image)
        titleTextView = findViewById(R.id.title_text_view)
        releaseDateTextView = findViewById(R.id.release_date_text_view)
        durationTextView = findViewById(R.id.duration_text_view)
        directorTextView = findViewById(R.id.director_text_view)
        buttonWatching = findViewById(R.id.button_watching)
        buttonWatchlist = findViewById(R.id.button_watchlist)
        buttonFavorite = findViewById(R.id.button_favorite)
        watchingTextView = findViewById(R.id.textView_watching)
        overviewTextView = findViewById(R.id.overview_text_view)
        seasonsRecyclerView = findViewById(R.id.seasons_recycler_view)
        castRecyclerView = findViewById(R.id.cast_recycler_view)
        viewFlipperSeries = findViewById(R.id.view_flipper_series)
        seriesRatingBar = findViewById(R.id.series_rating_bar)
        submitRatingButton = findViewById(R.id.submit_rating_button)
        reviewInputLayout = findViewById(R.id.review_text_input_layout)
        reviewEditText = findViewById(R.id.review_text_input_edit_text)
        editReviewButton = findViewById(R.id.edit_review_button)
        submitReviewButton = findViewById(R.id.submit_review_button)
        averageRatingBar = findViewById(R.id.average_rating_bar)
        buttonViewAllReviews = findViewById(R.id.button_view_all_reviews)

        viewFlipperSeries.displayedChild = 0

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
        val buttonColor = buttonWatching.backgroundTintList

        seriesDetailsViewModel.isSeriesInWatching.observe(this) { isWatching ->
            if (isWatching) {
                isSeriesInWatching = true
                buttonWatching.setBackgroundColor(colorTertiary)
                buttonWatching.setIconResource(R.drawable.round_play_circle_outline_24)
            } else {
                buttonWatching.setBackgroundColor(buttonColor!!.defaultColor)
                buttonWatching.setIconResource(R.drawable.round_remove_red_eye_24)
            }
        }

        seriesDetailsViewModel.isSeriesFavorited.observe(this) { isFavorited ->
            if (isFavorited) {
                buttonFavorite.setBackgroundColor(colorTertiary)
                buttonFavorite.setIconResource(R.drawable.ic_check)
            } else {
                buttonFavorite.setBackgroundColor(buttonColor!!.defaultColor)
                buttonFavorite.setIconResource(R.drawable.round_favorite_24)
            }
        }

        seriesDetailsViewModel.isSeriesInWatchlist.observe(this) { isInWatchlist ->
            if (isInWatchlist) {
                buttonWatchlist.setBackgroundColor(colorTertiary)
                buttonWatchlist.setIconResource(R.drawable.ic_check)
            } else {
                buttonWatchlist.setBackgroundColor(buttonColor!!.defaultColor)
                buttonWatchlist.setIconResource(R.drawable.round_more_time_24)
            }
        }

        seriesDetailsViewModel.isSeriesFinished.observe(this) { isFinished ->
            if (isFinished) {
                buttonWatching.isClickable = false
                buttonWatching.setBackgroundColor(colorTertiary)
                buttonWatching.setIconResource(R.drawable.ic_check)
                watchingTextView.text = "Completata"
            }
        }

        seriesDetailsViewModel.currentSeries.observe(this) {
            it.credits.cast = it.credits.cast.take(50)
            it.credits.crew =
                it.credits.crew.filter { crewMember -> crewMember.job == "Creator" || crewMember.job == "Series Director" }
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
            releaseDateTextView.text = "${it.releaseDate.substring(0, 4)} | Creata da:"
            durationTextView.text = "${it.duration} ep."
            if (it.creator.isNotEmpty()) {
                seriesDirectors = it.creator
                directorTextView.text =
                    seriesDirectors.joinToString(", ") { director -> director.name }
            } else {
                if (it.credits.crew.any { creator -> creator.job == "Creator" }) {
                    seriesDirectors = it.credits.crew.filter { creator -> creator.job == "Creator" }
                    directorTextView.text =
                        seriesDirectors.joinToString(", ") { creator -> creator.name }
                } else if (it.credits.crew.any { creator -> creator.job == "Series Director" }) {
                    seriesDirectors =
                        it.credits.crew.filter { creator -> creator.job == "Series Director" }
                    directorTextView.text =
                        seriesDirectors.joinToString(", ") { creator -> creator.name }
                } else {
                    seriesDirectors = emptyList()
                    directorTextView.text = "Non disponibile"
                }
            }
            seasonsRecyclerView.adapter =
                SeasonsAdapter(it.id, it.seasons, this, isSeriesInWatching)
            castRecyclerView.adapter = CastAdapter(it.credits.cast)

            if (it.title.length > 50) titleTextView.setOnClickListener { _ ->
                MaterialAlertDialogBuilder(this).setTitle("Titolo completo")
                    .setMessage(it.title)
                    .setPositiveButton("Ok", null)
                    .show()
            }
            viewFlipperSeries.displayedChild = 1
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
            if (seriesDirectors.isEmpty()) return@setOnClickListener
            else if (seriesDirectors.size == 1) {
                val directorId = seriesDirectors[0].id
                val intent = Intent(this, PersonDetailsActivity::class.java)
                intent.putExtra("personId", directorId)
                startActivity(intent)
            } else {
                MaterialAlertDialogBuilder(this).setTitle("Scegli un creatore")
                    .setItems(seriesDirectors.map { director -> director.name }
                        .toTypedArray()) { _, which ->
                        val directorId = seriesDirectors[which].id
                        val intent = Intent(this, PersonDetailsActivity::class.java)
                        intent.putExtra("personId", directorId)
                        startActivity(intent)
                    }.setNegativeButton("Annulla", null).show()
            }
        }

        seriesDetailsViewModel.isSeriesRated.observe(this) { isRated ->
            if (isRated) {
                seriesDetailsViewModel.loadCurrentSeriesRating(
                    UserUtils.getCurrentUserUid()!!, seriesId
                )
            }
        }

        seriesDetailsViewModel.isSeriesReviewed.observe(this) { isReviewed ->
            if (isReviewed) {
                seriesDetailsViewModel.loadCurrentSeriesReview(
                    UserUtils.getCurrentUserUid()!!, seriesId
                )
            }
        }

        seriesDetailsViewModel.currentSeriesRating.observe(this) { seriesRating ->
            if (seriesRating != null) {
                seriesRatingBar.rating = seriesRating.first
                submitRatingButton.isEnabled = false
                seriesRatingBar.setOnRatingBarChangeListener { _, rating, _ ->
                    submitRatingButton.isEnabled = seriesRatingBar.rating != seriesRating.first
                    submitRatingButton.setOnClickListener {
                        seriesDetailsViewModel.updateSeriesRating(
                            UserUtils.getCurrentUserUid()!!, seriesId, rating, Timestamp.now()
                        )
                        Toast.makeText(
                            this@SeriesDetailsActivity, "Valutazione aggiornata", Toast.LENGTH_SHORT
                        ).show()
                        submitRatingButton.isEnabled = false
                    }
                }
            } else {
                submitRatingButton.isEnabled = false
                seriesRatingBar.setOnRatingBarChangeListener { _, rating, _ ->
                    submitRatingButton.isEnabled = true
                    submitRatingButton.setOnClickListener {
                        seriesDetailsViewModel.updateSeriesRating(
                            UserUtils.getCurrentUserUid()!!, seriesId, rating, Timestamp.now()
                        )
                        Toast.makeText(
                            this@SeriesDetailsActivity, "Valutazione aggiunta", Toast.LENGTH_SHORT
                        ).show()
                        submitRatingButton.isEnabled = false
                    }
                }
            }
        }

        seriesDetailsViewModel.currentSeriesReview.observe(this) { seriesReview ->
            if (seriesReview != null) {
                editReviewButton.isEnabled = true
                submitReviewButton.isEnabled = false
                reviewEditText.setText(seriesReview.first)
                reviewEditText.isEnabled = false
                val date = seriesReview.second.toDate()
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
                    if (reviewText == seriesReview.first) {
                        reviewInputLayout.isErrorEnabled = true
                        reviewInputLayout.error = "La recensione è identica a quella precedente"
                        return@setOnClickListener
                    }
                    val timestamp = Timestamp.now()
                    seriesDetailsViewModel.updateSeriesReview(
                        UserUtils.getCurrentUserUid()!!, seriesId, reviewText, timestamp
                    )
                    Toast.makeText(
                        this@SeriesDetailsActivity, "Recensione aggiornata", Toast.LENGTH_SHORT
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
                    seriesDetailsViewModel.updateSeriesReview(
                        UserUtils.getCurrentUserUid()!!, seriesId, reviewText, timestamp
                    )
                    val format = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ITALY)
                    Toast.makeText(
                        this@SeriesDetailsActivity, "Recensione aggiunta", Toast.LENGTH_SHORT
                    ).show()
                    submitReviewButton.isEnabled = false
                    editReviewButton.isEnabled = true
                    reviewEditText.isEnabled = false
                    reviewInputLayout.helperText =
                        "Ultima modifica: ${format.format(timestamp.toDate())}"
                }
            }
        }

        seriesDetailsViewModel.averageSeriesRating.observe(this) {
            averageRatingBar.rating = it
        }

        seriesDetailsViewModel.seriesReviews.observe(this) {
            if (it.isEmpty()) {
                buttonViewAllReviews.isEnabled = false
                buttonViewAllReviews.text = "Vedi tutte le recensioni (0)"
            } else {
                buttonViewAllReviews.isEnabled = true
                buttonViewAllReviews.text = "Vedi tutte le recensioni (${it.size})"
                buttonViewAllReviews.setOnClickListener { _ ->
                    val intent = Intent(this, ViewAllActivity::class.java)
                    intent.putExtra("entities", ArrayList(it.reversed()))
                    intent.putExtra("title", "Recensioni")
                    startActivity(intent)
                }
            }
        }

        buttonWatching.setOnClickListener {
            seriesDetailsViewModel.toggleInWatching(seriesId)
        }

        buttonWatchlist.setOnClickListener {
            seriesDetailsViewModel.toggleWatchlist(seriesId)
        }

        buttonFavorite.setOnClickListener {
            seriesDetailsViewModel.toggleFavorite(seriesId)
        }
    }
}