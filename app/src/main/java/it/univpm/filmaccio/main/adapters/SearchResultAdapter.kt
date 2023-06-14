package it.univpm.filmaccio.main.adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.ShapeAppearanceModel
import it.univpm.filmaccio.R
import it.univpm.filmaccio.data.models.TmdbEntity
import it.univpm.filmaccio.data.models.User
import it.univpm.filmaccio.details.MovieDetailsActivity

class SearchResultAdapter : RecyclerView.Adapter<SearchResultAdapter.ViewHolder>() {

    companion object {
        const val RESULT_TYPE_TMDB_ENTITY = 0
        const val RESULT_TYPE_USER = 1
    }

    private var searchResults: List<Any> = listOf()

    @SuppressLint("NotifyDataSetChanged")
    fun updateSearchResults(newSearchResults: List<Any>) {
        searchResults = newSearchResults
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = when(viewType) {
            RESULT_TYPE_TMDB_ENTITY -> LayoutInflater.from(parent.context).inflate(R.layout.search_result_entity, parent, false)
            RESULT_TYPE_USER -> LayoutInflater.from(parent.context).inflate(R.layout.search_result_user, parent, false)
            else -> throw IllegalArgumentException("Invalid view type")
        }
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (val result = searchResults[position]) {
            is TmdbEntity -> {
                holder.title.text = result.title
                if (result.imagePath != null) {
                    Glide.with(holder.itemView.context)
                        .load("https://image.tmdb.org/t/p/w185${result.imagePath}")
                        .into(holder.shapeableImageView)
                } else {
                    Glide.with(holder.itemView.context)
                        .load(R.drawable.error_404)
                        .into(holder.shapeableImageView)
                }

                holder.itemView.setOnClickListener {
                    val context = holder.itemView.context
                    if (result.mediaType == "movie") {
                        val intent = Intent(context, MovieDetailsActivity::class.java)
                        intent.putExtra("movieId", result.id)
                        context.startActivity(intent)
                    } else {
                        Log.e("SearchResultAdapter", "Media type not supported")
                    }
                }
            }

            is User -> {
                holder.title.text = result.nameShown
                Glide.with(holder.itemView.context).load(result.profileImage)
                    .into(holder.shapeableImageView)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(searchResults[position]) {
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
