package it.univpm.filmaccio.details.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import it.univpm.filmaccio.R
import it.univpm.filmaccio.data.models.Person
import it.univpm.filmaccio.details.activities.MovieDetailsActivity
import it.univpm.filmaccio.details.activities.SeriesDetailsActivity

class ProductsAdapter(private val products: List<Person.Product>) :
    RecyclerView.Adapter<ProductsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        LayoutInflater.from(parent.context)
            .inflate(R.layout.search_result_entity, parent, false).apply {
                return ViewHolder(this)
            }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = products[position]
        holder.title.text = product.title
        if (product.posterPath != null) {
            Glide.with(holder.itemView.context)
                .load("https://image.tmdb.org/t/p/w185${product.posterPath}")
                .into(holder.shapeableImageView)
        } else {
            Glide.with(holder.itemView.context)
                .load(R.drawable.error_404)
                .into(holder.shapeableImageView)
        }
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            if (product.mediaType == "movie") {
                val intent = Intent(context, MovieDetailsActivity::class.java)
                intent.putExtra("movieId", product.id)
                context.startActivity(intent)
            } else if (product.mediaType == "tv") {
                val intent = Intent(context, SeriesDetailsActivity::class.java)
                intent.putExtra("seriesId", product.id)
                context.startActivity(intent)
            }

        }
    }

    override fun getItemCount(): Int = products.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val shapeableImageView: ShapeableImageView = view.findViewById(R.id.search_result_image)
        val title: TextView = view.findViewById(R.id.search_result_title)
    }
}
