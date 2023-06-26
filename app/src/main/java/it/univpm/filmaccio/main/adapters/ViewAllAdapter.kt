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
import it.univpm.filmaccio.data.models.Series
import it.univpm.filmaccio.data.models.TmdbEntity
import it.univpm.filmaccio.data.models.User
import it.univpm.filmaccio.details.activities.MovieDetailsActivity
import it.univpm.filmaccio.details.activities.PersonDetailsActivity
import it.univpm.filmaccio.details.activities.SeriesDetailsActivity
import it.univpm.filmaccio.details.activities.UserDetailsActivity

class ViewAllAdapter : RecyclerView.Adapter<ViewAllAdapter.ViewHolder>() {

    companion object {
        const val TYPE_TMDB_ENTITY = 0
        const val TYPE_USER = 1
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

            else -> throw IllegalArgumentException("Invalid view type")
        }
        return ViewHolder(view)
    }

    // In questo metodo impostiamo le varie informazioni sul risultato di ricerca nel corrispondente
    // layout in modo da poterle visualizzare correttamente nella RecyclerView.
    // Quindi se il risultato è TmdbEntiy impostiamo il titolo e l'immagine, se è un utente impostiamo
    // la foto profilo e il nome (visualizzato).
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (val entity = entities[position]) {
            is Movie -> {
                holder.title.text = entity.title
                if (entity.posterPath != null) {
                    Glide.with(holder.itemView.context)
                        .load("https://image.tmdb.org/t/p/w185${entity.posterPath}")
                        .into(holder.shapeableImageView)
                } else {
                    Glide.with(holder.itemView.context)
                        .load(R.drawable.error_404)
                        .into(holder.shapeableImageView)
                }

                holder.itemView.setOnClickListener {
                    val context = holder.itemView.context
                        val intent = Intent(context, MovieDetailsActivity::class.java)
                        intent.putExtra("movieId", entity.id)
                        context.startActivity(intent)
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
                    intent.putExtra("nameShown",entity.nameShown)
                    intent.putExtra("username",entity.username)
                    intent.putExtra("backdropImage",entity.backdropImage)
                    intent.putExtra("profileImage",entity.profileImage)
                    context.startActivity(intent)
                }

            }
        }
    }

    // Con questo metodo ricaviamo di che tipo si tratta il risultato di un certo elemento della lista.
    override fun getItemViewType(position: Int): Int {
        return when (entities[position]) {
            is Movie -> TYPE_TMDB_ENTITY
            is Series -> TYPE_TMDB_ENTITY
            is Person -> TYPE_TMDB_ENTITY
            is User -> TYPE_USER
            is String -> TYPE_USER
            else -> throw IllegalArgumentException("Invalid item type")
        }
    }

    override fun getItemCount(): Int = entities.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val shapeableImageView: ShapeableImageView = view.findViewById(R.id.search_result_image)
        val title: TextView = view.findViewById(R.id.search_result_title)
    }
}