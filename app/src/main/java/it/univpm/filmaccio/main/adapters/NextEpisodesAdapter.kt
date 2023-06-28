package it.univpm.filmaccio.main.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import it.univpm.filmaccio.R
import it.univpm.filmaccio.data.models.NextEpisode
import it.univpm.filmaccio.details.adapters.EpisodesAdapter
import it.univpm.filmaccio.main.utils.FirestoreService
import it.univpm.filmaccio.main.utils.UserUtils

class NextEpisodesAdapter(
    private val nextEpisodes: List<NextEpisode>,
    private val context: Context
) : RecyclerView.Adapter<NextEpisodesAdapter.NextEpisodeViewHolder>() {

    class NextEpisodeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val seriesNameTextView: TextView = view.findViewById(R.id.episode_item_series_name)
        val seasonEpisodeNumbers: TextView =
            view.findViewById(R.id.episode_item_season_number_episode_number)
        val episodeNameTextView: TextView = view.findViewById(R.id.episode_item_episode_name)
        val image: ShapeableImageView = view.findViewById(R.id.episode_item_image)
        val buttonWatchEpisode: MaterialButton =
            view.findViewById(R.id.episode_item_button_watch_episode)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NextEpisodeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.episode_item, parent, false)
        return NextEpisodeViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: NextEpisodeViewHolder, position: Int) {
        val uid = UserUtils.getCurrentUserUid()!!
        val episode = nextEpisodes[position]
        holder.seriesNameTextView.text = episode.seriesTitle
        holder.seasonEpisodeNumbers.text = "S${episode.seasonNumber} | E${episode.episodeNumber}"
        holder.episodeNameTextView.text = episode.episodeTitle
        val posterPath = episode.posterPath
        if (posterPath != null) Glide.with(holder.image.context)
            .load("https://image.tmdb.org/t/p/w185$posterPath").into(holder.image)
        else Glide.with(holder.image.context).load(R.drawable.error_404).into(holder.image)

        holder.buttonWatchEpisode.setOnClickListener {
            FirestoreService.addWatchedEpisode(
                uid,
                episode.seriesId,
                episode.seasonNumber,
                episode.episodeNumber
            )
        }

    }

    override fun getItemCount() = nextEpisodes.size
}