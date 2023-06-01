package it.univpm.filmaccio.main.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.ShapeAppearanceModel
import it.univpm.filmaccio.R
import it.univpm.filmaccio.data.models.TmdbEntity
import it.univpm.filmaccio.data.models.User

class SearchResultAdapter : RecyclerView.Adapter<SearchResultAdapter.ViewHolder>() {

    private var searchResults: List<Any> = listOf()

    @SuppressLint("NotifyDataSetChanged")
    fun updateSearchResults(newSearchResults: List<Any>) {
        searchResults = newSearchResults
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.search_result_row, parent, false)
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
            }

            is User -> {
                holder.title.text = result.nameShown
                val shapeAppearanceModel =
                    holder.shapeableImageView.shapeAppearanceModel.toBuilder()
                        .setAllCornerSizes(ShapeAppearanceModel.PILL)
                        .build()
                holder.shapeableImageView.shapeAppearanceModel = shapeAppearanceModel
                Glide.with(holder.itemView.context).load(result.profileImage)
                    .into(holder.shapeableImageView)
            }
        }
    }

    override fun getItemCount(): Int = searchResults.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val shapeableImageView: ShapeableImageView = view.findViewById(R.id.search_result_image)
        val title: TextView = view.findViewById(R.id.search_result_title)
    }
}
