package it.univpm.filmaccio.details.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import it.univpm.filmaccio.R
import it.univpm.filmaccio.data.models.Series
import it.univpm.filmaccio.data.repository.SeriesRepository
import it.univpm.filmaccio.details.viewmodels.EpisodesViewModel
import it.univpm.filmaccio.main.utils.FirestoreService
import it.univpm.filmaccio.main.utils.UserUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EpisodesAdapter(
    private val episodes: List<Series.Episode>,
    private val seriesId: Long,
    private val seasonNumber: Long,
    private val context: Context,
    private val isSeriesInWatching: Boolean
) : RecyclerView.Adapter<EpisodesAdapter.EpisodeViewHolder>() {

    private val seriesRepository = SeriesRepository()

    class EpisodeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.episode_item_name)
        val seasonEpisodeNumbers: TextView =
            view.findViewById(R.id.episode_item_season_number_episode_number)
        val overview: TextView = view.findViewById(R.id.episode_item_overview)
        val image: ShapeableImageView = view.findViewById(R.id.episode_item_image)
        val buttonWatchEpisode: MaterialButton =
            view.findViewById(R.id.episode_item_button_watch_episode)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.series_details_episode_item, parent, false)
        return EpisodeViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: EpisodeViewHolder, position: Int) {
        val uid = UserUtils.getCurrentUserUid()!!
        val episode = episodes[position]
        val episodeViewModel = EpisodesViewModel(seriesId, seasonNumber, episode.number)
        holder.name.text = episode.name
        holder.seasonEpisodeNumbers.text = "S$seasonNumber | E${episode.number}"
        if (episode.overview == "") holder.overview.text = "Descrizione non disponibile"
        else holder.overview.text = episode.overview
        if (episode.imagePath != null) Glide.with(holder.image.context)
            .load("https://image.tmdb.org/t/p/w185${episode.imagePath}").into(holder.image)
        else Glide.with(holder.image.context).load(R.drawable.error_404).into(holder.image)

        val typedValuePrimary = TypedValue()
        val typedValueTertiary = TypedValue()
        val theme = context.theme
        theme.resolveAttribute(
            com.google.android.material.R.attr.colorPrimary, typedValuePrimary, true
        )
        theme.resolveAttribute(
            com.google.android.material.R.attr.colorTertiary, typedValueTertiary, true
        )
        val color = typedValuePrimary.data
        val colorTertiary = typedValueTertiary.data

        var isEpisodeWatched = false

        CoroutineScope(Dispatchers.Main).launch {
            episodeViewModel.isEpisodeWatched.collect {
                isEpisodeWatched = if (it) {
                    holder.buttonWatchEpisode.setBackgroundColor(colorTertiary)
                    holder.buttonWatchEpisode.setIconResource(R.drawable.ic_check)
                    true
                } else {
                    holder.buttonWatchEpisode.setBackgroundColor(color)
                    holder.buttonWatchEpisode.setIconResource(R.drawable.round_remove_red_eye_24)
                    false
                }
            }
        }

        holder.overview.setOnClickListener {
            if (holder.overview.maxLines == 1) {
                holder.overview.maxLines = 100
            } else {
                holder.overview.maxLines = 1
            }
        }

        holder.buttonWatchEpisode.setOnClickListener {
            isEpisodeWatched = if (isEpisodeWatched) {
                FirestoreService.removeEpisodeFromWatched(
                    uid, seriesId, seasonNumber, episode.number
                )
                holder.buttonWatchEpisode.setBackgroundColor(color)
                holder.buttonWatchEpisode.setIconResource(R.drawable.round_remove_red_eye_24)
                false
            } else {
                if (!isSeriesInWatching) {
                    FirestoreService.addToList(uid, "watching_t", seriesId)
                }
                FirestoreService.addWatchedEpisode(uid, seriesId, seasonNumber, episode.number)
                holder.buttonWatchEpisode.setBackgroundColor(colorTertiary)
                holder.buttonWatchEpisode.setIconResource(R.drawable.ic_check)
                true
            }
            CoroutineScope(Dispatchers.Main).launch {
                seriesRepository.checkIfSeriesFinished(uid, seriesId)
            }
        }

    }

    override fun getItemCount() = episodes.size
}
