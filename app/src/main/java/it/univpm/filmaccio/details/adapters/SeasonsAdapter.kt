package it.univpm.filmaccio.details.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.ChangeBounds
import androidx.transition.Fade
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import it.univpm.filmaccio.R
import it.univpm.filmaccio.data.models.Series

/**
 * Adapter per la RecyclerView che mostra le stagioni di una serie
 *
 * @param seriesId id della serie
 * @param seasons lista di stagioni
 * @param context contesto
 * @param isSeriesInWatching true se la serie è nella lista "In visione" dell'utente, false altrimenti
 *
 * @author nicolobartolinii
 */
class SeasonsAdapter(
    private val seriesId: Long,
    private val seasons: List<Series.Season>,
    private val context: Context,
    private val isSeriesInWatching: Boolean
) : RecyclerView.Adapter<SeasonsAdapter.SeasonViewHolder>() {

    class SeasonViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var poster: ShapeableImageView = view.findViewById(R.id.seasons_list_element_image)
        val title: TextView = view.findViewById(R.id.season_list_element_title_textview)
        val infos: TextView = view.findViewById(R.id.season_list_element_info_textview)
        val overview: TextView = view.findViewById(R.id.season_list_element_overview_textview)
        val divider: View = view.findViewById(R.id.season_list_element_divider)
        val episodesRecyclerView: RecyclerView =
            view.findViewById(R.id.season_list_element_episodes_recyclerview)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: SeasonViewHolder, position: Int) {
        val season = seasons[position]
        if (season.posterPath != null) Glide.with(context)
            .load("https://image.tmdb.org/t/p/w185${season.posterPath}").centerInside()
            .into(holder.poster)
        else holder.poster.setImageResource(R.drawable.error_404)
        holder.title.text = season.name
        val episodesNumber = season.episodes.size
        var episodesText = "episodi"
        if (episodesNumber == 1) episodesText = "episodio"
        if (season.releaseDate != null) holder.infos.text =
            "${season.releaseDate?.substring(0, 4)} | $episodesNumber $episodesText"
        else holder.infos.text = "$episodesNumber $episodesText"
        if (season.overview == "") holder.overview.text = "Nessuna descrizione disponibile"
        else holder.overview.text = season.overview

        val originalParams = holder.poster.layoutParams as LinearLayout.LayoutParams
        val originalMargins = originalParams.marginEnd
        val expandedParams = LinearLayout.LayoutParams(dpToPx(80), dpToPx(120))
        expandedParams.marginEnd = originalMargins
        val collapsedParams = LinearLayout.LayoutParams(dpToPx(80), dpToPx(80))
        collapsedParams.marginEnd = originalMargins

        val transitionSet = TransitionSet().apply {
            addTransition(ChangeBounds().setDuration(300))
            addTransition(
                Fade().addTarget(holder.divider).addTarget(holder.episodesRecyclerView)
                    .setStartDelay(300).setDuration(300)
            )
        }

        holder.itemView.setOnClickListener {
            TransitionManager.beginDelayedTransition(holder.itemView as ViewGroup, transitionSet)
            if (holder.episodesRecyclerView.visibility == View.GONE) {
                holder.poster.layoutParams = expandedParams
                holder.overview.maxLines = Int.MAX_VALUE
                holder.divider.visibility = View.VISIBLE
                holder.episodesRecyclerView.visibility = View.VISIBLE
            } else {
                holder.poster.layoutParams = collapsedParams
                holder.overview.maxLines = 2
                holder.divider.visibility = View.GONE
                holder.episodesRecyclerView.visibility = View.GONE
            }
        }

        val episodesAdapter =
            EpisodesAdapter(season.episodes, seriesId, season.number, context, isSeriesInWatching)
        holder.episodesRecyclerView.adapter = episodesAdapter
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeasonViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.seasons_list_element, parent, false)
        return SeasonViewHolder(view)
    }

    override fun getItemCount() = seasons.size

    private fun dpToPx(dp: Int): Int {
        val density = Resources.getSystem().displayMetrics.density
        return (dp.toFloat() * density).toInt()
    }

}
