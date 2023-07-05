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
import it.univpm.filmaccio.data.models.TmdbEntity
import it.univpm.filmaccio.data.models.User
import it.univpm.filmaccio.details.activities.MovieDetailsActivity
import it.univpm.filmaccio.details.activities.PersonDetailsActivity
import it.univpm.filmaccio.details.activities.SeriesDetailsActivity
import it.univpm.filmaccio.details.activities.UserDetailsActivity

// Questa classe è un adapter per la RecyclerView che mostra i risultati della ricerca.
// Ho spiegato a cosa servono gli adapter nei commenti di SearchFragment.
class SearchResultAdapter : RecyclerView.Adapter<SearchResultAdapter.ViewHolder>() {

    // Questo companion object contiene delle costanti che rappresentano i tipi di risultati
    // che possono essere mostrati nella RecyclerView.
    // Se il risultato è una TMDBEntity, allora il tipo è RESULT_TYPE_TMDB_ENTITY e quindi il layout
    // dell'elemento della lista sarà del tipo per le entity. Se il risultato è un utente, allora
    // il tipo è RESULT_TYPE_USER e quindi il layout dell'elemento della lista sarà del tipo per gli
    // utenti.
    companion object {
        const val RESULT_TYPE_TMDB_ENTITY = 0
        const val RESULT_TYPE_USER = 1
    }

    // Qui inizializziamo la variabile dei risultati di ricerca con una lista vuota.
    private var searchResults: List<Any> = listOf()

    // Questo metodo serve per aggiornare i risultati di ricerca (viene chiamato in SearchFragment).
    @SuppressLint("NotifyDataSetChanged")
    fun updateSearchResults(newSearchResults: List<Any>) {
        searchResults = newSearchResults
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = when (viewType) {
            // Qui viene scelto il layout da usare in base al tipo di risultato.
            RESULT_TYPE_TMDB_ENTITY -> LayoutInflater.from(parent.context)
                .inflate(R.layout.search_result_entity, parent, false)

            RESULT_TYPE_USER -> LayoutInflater.from(parent.context)
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
        when (val result = searchResults[position]) {
            is TmdbEntity -> {
                holder.title.text = result.title
                if (result.imagePath != null) {
                    Glide.with(holder.itemView.context)
                        .load("https://image.tmdb.org/t/p/w185${result.imagePath}")
                        .into(holder.shapeableImageView)
                } else {
                    Glide.with(holder.itemView.context).load(R.drawable.error_404)
                        .into(holder.shapeableImageView)
                }

                holder.itemView.setOnClickListener {
                    val context = holder.itemView.context
                    when (result.mediaType) {
                        "movie" -> {
                            val intent = Intent(context, MovieDetailsActivity::class.java)
                            intent.putExtra("movieId", result.id)
                            context.startActivity(intent)
                        }

                        "tv" -> {
                            val intent = Intent(context, SeriesDetailsActivity::class.java)
                            intent.putExtra("seriesId", result.id)
                            context.startActivity(intent)
                        }

                        "person" -> {
                            val intent = Intent(context, PersonDetailsActivity::class.java)
                            intent.putExtra("personId", result.id)
                            context.startActivity(intent)
                        }
                    }
                }
            }

            is User -> {
                holder.title.text = result.nameShown
                Glide.with(holder.itemView.context).load(result.profileImage)
                    .into(holder.shapeableImageView)
                holder.itemView.setOnClickListener {
                    val context = holder.itemView.context
                    val intent = Intent(context, UserDetailsActivity::class.java)
                    intent.putExtra("uid", result.uid)
                    intent.putExtra("nameShown", result.nameShown)
                    intent.putExtra("username", result.username)
                    intent.putExtra("backdropImage", result.backdropImage)
                    intent.putExtra("profileImage", result.profileImage)
                    context.startActivity(intent)
                }

            }
        }
    }

    // Con questo metodo ricaviamo di che tipo si tratta il risultato di un certo elemento della lista.
    override fun getItemViewType(position: Int): Int {
        return when (searchResults[position]) {
            is TmdbEntity -> RESULT_TYPE_TMDB_ENTITY
            is User -> RESULT_TYPE_USER
            else -> throw IllegalArgumentException("Invalid item type")
        }
    }

    override fun getItemCount(): Int = searchResults.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val shapeableImageView: ShapeableImageView = view.findViewById(R.id.search_result_image)
        val title: TextView = view.findViewById(R.id.search_result_title)
    }
}
