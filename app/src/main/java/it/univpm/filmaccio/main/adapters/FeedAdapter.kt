package it.univpm.filmaccio.main.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import it.univpm.filmaccio.R
import it.univpm.filmaccio.data.models.ReviewTriple
import it.univpm.filmaccio.data.models.TmdbEntity
import it.univpm.filmaccio.details.activities.MovieDetailsActivity
import it.univpm.filmaccio.details.activities.SeriesDetailsActivity
import it.univpm.filmaccio.details.activities.UserDetailsActivity

/**
 * Questa classe è l'adapter che gestisce la RecyclerView presente nel FeedFragment.
 * In breve, mostra le recensioni degli utenti che l'utente segue.
 * Ogni recensione è composta da un'immagine del prodotto recensito, l'immagine del profilo dell'utente che ha
 * lasciato la recensione, il nome dell'utente, la data in cui è stata lasciata la recensione e il testo della recensione.
 * Cliccando sull'immagine del prodotto si viene reindirizzati alla pagina di dettaglio del prodotto.
 * Cliccando sull'immagine del profilo si viene reindirizzati alla pagina di dettaglio dell'utente.
 *
 * @param feed lista di coppie (recensione, prodotto recensito)
 * @param context contesto dell'activity
 *
 * @author nicolobartolinii
 */
class FeedAdapter(
    private val feed: List<Pair<ReviewTriple, TmdbEntity>>, private val context: Context
) : RecyclerView.Adapter<FeedAdapter.FeedViewHolder>() {

    class FeedViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userImage: ShapeableImageView = view.findViewById(R.id.review_user_image)
        val userTextView: TextView = view.findViewById(R.id.review_user_text_view)
        val reviewInfo: TextView = view.findViewById(R.id.review_info_text_view)
        val reviewText: TextView = view.findViewById(R.id.review_text_view)
        val productImage: ShapeableImageView = view.findViewById(R.id.review_product_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.feed_review_item, parent, false)
        return FeedViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        val review = feed[position]
        if (review.second.imagePath != null) {
            Glide.with(context).load("https://image.tmdb.org/t/p/w500${review.second.imagePath}")
                .into(holder.productImage)
        } else {
            Glide.with(context).load(R.drawable.error_404).into(holder.productImage)
        }
        Glide.with(context).load(review.first.user.profileImage).into(holder.userImage)
        holder.userTextView.text = review.first.user.nameShown
        holder.reviewInfo.text = "Recensione lasciata il: ${review.first.date}"
        holder.reviewText.text = review.first.review

        holder.userImage.setOnClickListener {
            val intent = Intent(context, UserDetailsActivity::class.java)
            intent.putExtra("uid", review.first.user.uid)
            intent.putExtra("nameShown", review.first.user.nameShown)
            intent.putExtra("username", review.first.user.username)
            intent.putExtra("profileImage", review.first.user.profileImage)
            intent.putExtra("backdropImage", review.first.user.backdropImage)
            context.startActivity(intent)
        }

        holder.productImage.setOnClickListener {
            if (review.second.mediaType == "movie") {
                val intent = Intent(context, MovieDetailsActivity::class.java)
                intent.putExtra("movieId", review.second.id)
                context.startActivity(intent)
            } else {
                val intent = Intent(context, SeriesDetailsActivity::class.java)
                intent.putExtra("seriesId", review.second.id)
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount() = feed.size
}