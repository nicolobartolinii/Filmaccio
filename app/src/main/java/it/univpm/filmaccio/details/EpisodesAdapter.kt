package it.univpm.filmaccio.details

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import it.univpm.filmaccio.R
import it.univpm.filmaccio.data.models.Series

class EpisodesAdapter(
    private val episodes: List<Series.Episode>,
    private val seasonNumber: Int
) : RecyclerView.Adapter<EpisodesAdapter.EpisodeViewHolder>() {

    class EpisodeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.episode_item_name)
        val seasonEpisodeNumbers: TextView =
            view.findViewById(R.id.episode_item_season_number_episode_number)
        val overview: TextView = view.findViewById(R.id.episode_item_overview)
        val image: ShapeableImageView = view.findViewById(R.id.episode_item_image)
        val buttonWatchEpisode: Button = view.findViewById(R.id.episode_item_button_watch_episode)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.series_details_episode_item, parent, false)
        return EpisodeViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: EpisodeViewHolder, position: Int) {
        val episode = episodes[position]
        holder.name.text = episode.name
        holder.seasonEpisodeNumbers.text = "S$seasonNumber | E${episode.number}"
        if (episode.overview == "") holder.overview.text = "Descrizione non disponibile"
        else holder.overview.text = episode.overview
        if (episode.imagePath != null) Glide.with(holder.image.context)
            .load("https://image.tmdb.org/t/p/w185${episode.imagePath}")
            .into(holder.image)
        else Glide.with(holder.image.context)
            .load(R.drawable.error_404)
            .into(holder.image)

        holder.overview.setOnClickListener {
            if (holder.overview.maxLines == 1) {
                holder.overview.maxLines = 100
            } else {
                holder.overview.maxLines = 1
            }
        }
    }

    override fun getItemCount() = episodes.size
}
