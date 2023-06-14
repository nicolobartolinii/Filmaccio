package it.univpm.filmaccio.details

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import it.univpm.filmaccio.R
import it.univpm.filmaccio.data.models.Character

class CastAdapter(private val cast: List<Character>) :
    RecyclerView.Adapter<CastAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val personImage: ShapeableImageView = itemView.findViewById(R.id.person_image)
        val nameCastTextView: TextView = itemView.findViewById(R.id.name_cast_text_view)
        val characterNameCastTextView: TextView =
            itemView.findViewById(R.id.character_name_cast_text_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cast_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val castMember = cast[position]
        if (castMember.profilePath != null) {
            Glide.with(holder.personImage.context)
                .load("https://image.tmdb.org/t/p/w185${castMember.profilePath}")
                .into(holder.personImage)
        } else {
            Glide.with(holder.itemView.context)
                .load(R.drawable.error_404)
                .into(holder.personImage)
        }
        holder.nameCastTextView.text = castMember.name
        holder.characterNameCastTextView.text = castMember.character
    }

    override fun getItemCount() = cast.size
}
