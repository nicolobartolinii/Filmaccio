package it.univpm.filmaccio.main.adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import it.univpm.filmaccio.R
import it.univpm.filmaccio.data.models.Movie
import it.univpm.filmaccio.data.models.Person
import it.univpm.filmaccio.data.models.ReviewTriple
import it.univpm.filmaccio.data.models.Series
import it.univpm.filmaccio.data.models.TmdbEntity
import it.univpm.filmaccio.data.models.User
import it.univpm.filmaccio.data.repository.MovieRepository
import it.univpm.filmaccio.data.repository.SeriesRepository
import it.univpm.filmaccio.details.activities.MovieDetailsActivity
import it.univpm.filmaccio.details.activities.SeriesDetailsActivity
import it.univpm.filmaccio.details.activities.UserDetailsActivity
import it.univpm.filmaccio.main.utils.FirestoreService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Questa classe è l'adapter che gestisce la RecyclerView presente nella ViewAllActivity.
 * In breve, mostra una lista di molti tipi di entità (serie, film, persone, utenti, recensioni).
 *
 * @author nicolobartolinii
 * @author NicolaPiccia
 */
class ViewAllAdapter(private val type: Char = 'm') :
    RecyclerView.Adapter<ViewAllAdapter.ViewHolder>() {

    companion object {
        const val TYPE_TMDB_ENTITY = 0
        const val TYPE_USER = 1
        const val TYPE_REVIEW = 2
    }

    private var entities: List<Any> = listOf()

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(newEntities: List<Any>) {
        entities = newEntities
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = when (viewType) {
            TYPE_TMDB_ENTITY -> LayoutInflater.from(parent.context)
                .inflate(R.layout.search_result_entity, parent, false)

            TYPE_USER -> LayoutInflater.from(parent.context)
                .inflate(R.layout.search_result_user, parent, false)

            TYPE_REVIEW -> {
                return ReviewViewHolder(
                    LayoutInflater.from(parent.context).inflate(R.layout.review_item, parent, false)
                )
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
        return ViewHolder(view)
    }

    // In questo metodo impostiamo le varie informazioni sul risultato di ricerca nel corrispondente
    // layout in modo da poterle visualizzare correttamente nella RecyclerView.
    // Quindi se il risultato è TmdbEntiy impostiamo il titolo e l'immagine, se è un utente impostiamo
    // la foto profilo e il nome (visualizzato).
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (val entity = entities[position]) {
            is Movie -> {
                holder.title.text = entity.title
                if (entity.posterPath != null) {
                    Glide.with(holder.itemView.context)
                        .load("https://image.tmdb.org/t/p/w185${entity.posterPath}")
                        .into(holder.shapeableImageView)
                } else {
                    Glide.with(holder.itemView.context).load(R.drawable.error_404)
                        .into(holder.shapeableImageView)
                }

                holder.itemView.setOnClickListener {
                    val context = holder.itemView.context
                    val intent = Intent(context, MovieDetailsActivity::class.java)
                    intent.putExtra("movieId", entity.id)
                    context.startActivity(intent)
                }
            }

            is Series -> {
                holder.title.text = entity.title
                if (entity.posterPath != null) {
                    Glide.with(holder.itemView.context)
                        .load("https://image.tmdb.org/t/p/w185${entity.posterPath}")
                        .into(holder.shapeableImageView)
                } else {
                    Glide.with(holder.itemView.context).load(R.drawable.error_404)
                        .into(holder.shapeableImageView)
                }

                holder.itemView.setOnClickListener {
                    val context = holder.itemView.context
                    val intent = Intent(context, SeriesDetailsActivity::class.java)
                    intent.putExtra("seriesId", entity.id)
                    context.startActivity(intent)
                }
            }

            is TmdbEntity -> {
                if (entity.mediaType == "series") {
                    holder.title.text = entity.title
                    if (entity.imagePath != null) {
                        Glide.with(holder.itemView.context)
                            .load("https://image.tmdb.org/t/p/w185${entity.imagePath}")
                            .into(holder.shapeableImageView)
                    } else {
                        Glide.with(holder.itemView.context).load(R.drawable.error_404)
                            .into(holder.shapeableImageView)
                    }

                    holder.itemView.setOnClickListener {
                        val context = holder.itemView.context
                        val intent = Intent(context, SeriesDetailsActivity::class.java)
                        intent.putExtra("seriesId", entity.id)
                        context.startActivity(intent)
                    }
                } else if (entity.mediaType == "movie") {
                    holder.title.text = entity.title
                    if (entity.imagePath != null) {
                        Glide.with(holder.itemView.context)
                            .load("https://image.tmdb.org/t/p/w185${entity.imagePath}")
                            .into(holder.shapeableImageView)
                    } else {
                        Glide.with(holder.itemView.context).load(R.drawable.error_404)
                            .into(holder.shapeableImageView)
                    }

                    holder.itemView.setOnClickListener {
                        val context = holder.itemView.context
                        val intent = Intent(context, MovieDetailsActivity::class.java)
                        intent.putExtra("movieId", entity.id)
                        context.startActivity(intent)
                    }
                }
            }

            is Long -> {
                if (type == 'm') {
                    val movieRepository = MovieRepository()
                    CoroutineScope(Dispatchers.Main).launch {
                        val movieDetails = movieRepository.getMovieDetails(entity)
                        holder.title.text = movieDetails.title
                        if (movieDetails.posterPath != null) {
                            Glide.with(holder.itemView.context)
                                .load("https://image.tmdb.org/t/p/w185${movieDetails.posterPath}")
                                .into(holder.shapeableImageView)
                        } else {
                            Glide.with(holder.itemView.context).load(R.drawable.error_404)
                                .into(holder.shapeableImageView)
                        }

                        holder.itemView.setOnClickListener {
                            val context = holder.itemView.context
                            val intent = Intent(context, MovieDetailsActivity::class.java)
                            intent.putExtra("movieId", entity)
                            context.startActivity(intent)
                        }
                    }
                } else {
                    val seriesRepository = SeriesRepository()
                    CoroutineScope(Dispatchers.Main).launch {
                        val seriesDetails = seriesRepository.getSeriesDetails(entity)
                        holder.title.text = seriesDetails.title
                        if (seriesDetails.posterPath != null) {
                            Glide.with(holder.itemView.context)
                                .load("https://image.tmdb.org/t/p/w185${seriesDetails.posterPath}")
                                .into(holder.shapeableImageView)
                        } else {
                            Glide.with(holder.itemView.context).load(R.drawable.error_404)
                                .into(holder.shapeableImageView)
                        }

                        holder.itemView.setOnClickListener {
                            val context = holder.itemView.context
                            val intent = Intent(context, SeriesDetailsActivity::class.java)
                            intent.putExtra("seriesId", entity)
                            context.startActivity(intent)
                        }
                    }
                }
            }

            is User -> {
                holder.title.text = entity.nameShown
                Glide.with(holder.itemView.context).load(entity.profileImage)
                    .into(holder.shapeableImageView)
                holder.itemView.setOnClickListener {
                    val context = holder.itemView.context
                    val intent = Intent(context, UserDetailsActivity::class.java)
                    intent.putExtra("uid", entity.uid)
                    intent.putExtra("nameShown", entity.nameShown)
                    intent.putExtra("username", entity.username)
                    intent.putExtra("backdropImage", entity.backdropImage)
                    intent.putExtra("profileImage", entity.profileImage)
                    context.startActivity(intent)
                }

            }

            is String -> {
                CoroutineScope(Dispatchers.Main).launch {
                    FirestoreService.getUserByUid(entity).collect { user ->
                        withContext(Dispatchers.Main) {
                            holder.title.text = user?.nameShown
                            Glide.with(holder.itemView.context).load(user?.profileImage)
                                .into(holder.shapeableImageView)
                            holder.itemView.setOnClickListener {
                                val context = holder.itemView.context
                                val intent = Intent(context, UserDetailsActivity::class.java)
                                intent.putExtra("uid", user?.uid)
                                intent.putExtra("nameShown", user?.nameShown)
                                intent.putExtra("username", user?.username)
                                intent.putExtra("backdropImage", user?.backdropImage)
                                intent.putExtra("profileImage", user?.profileImage)
                                context.startActivity(intent)
                            }
                        }
                    }
                }
            }

            is ReviewTriple -> {
                holder as ReviewViewHolder
                holder.title.text = "Ha recensito il film il: ${entity.date}"
                Glide.with(holder.itemView.context).load(entity.user.profileImage)
                    .into(holder.shapeableImageView)
                holder.shapeableImageView.setOnClickListener {
                    val intent = Intent(holder.itemView.context, UserDetailsActivity::class.java)
                    intent.putExtra("uid", entity.user.uid)
                    intent.putExtra("nameShown", entity.user.nameShown)
                    intent.putExtra("username", entity.user.username)
                    intent.putExtra("backdropImage", entity.user.backdropImage)
                    intent.putExtra("profileImage", entity.user.profileImage)
                    holder.itemView.context.startActivity(intent)
                }
                holder.reviewUser.text = entity.user.nameShown
                holder.reviewText.text = entity.review
            }
        }
    }

    // Con questo metodo ricaviamo di che tipo si tratta il risultato di un certo elemento della lista.
    override fun getItemViewType(position: Int): Int {
        return when (entities[position]) {
            is Movie -> TYPE_TMDB_ENTITY
            is Series -> TYPE_TMDB_ENTITY
            is Person -> TYPE_TMDB_ENTITY
            is Long -> TYPE_TMDB_ENTITY
            is TmdbEntity -> TYPE_TMDB_ENTITY
            is User -> TYPE_USER
            is String -> TYPE_USER
            is ReviewTriple -> TYPE_REVIEW
            else -> throw IllegalArgumentException("Invalid item type")
        }
    }

    override fun getItemCount(): Int = entities.size

    open class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        open val shapeableImageView: ShapeableImageView =
            view.findViewById(R.id.search_result_image) ?: view.findViewById(R.id.review_user_image)
        open val title: TextView = view.findViewById(R.id.search_result_title) ?: view.findViewById(
            R.id.review_info_text_view
        )
    }

    class ReviewViewHolder(view: View) : ViewHolder(view) {
        val reviewText: TextView = view.findViewById(R.id.review_text_view)
        val reviewUser: TextView = view.findViewById(R.id.review_user_text_view)
    }
}