package it.univpm.filmaccio.main.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import com.google.android.material.imageview.ShapeableImageView
import it.univpm.filmaccio.R
import it.univpm.filmaccio.data.models.ProfileListItem
import it.univpm.filmaccio.main.activities.ViewAllActivity

/**
 * Questa classe è l'adapter che gestisce la RecyclerView presente nel ProfileFragment.
 * In breve, mostra le liste di film e serie TV salvate dall'utente.
 *
 * @author nicolobartolinii
 */
class ProfileHorizontalListAdapter(
    private val userLists: Map<String, List<Long>>, private val context: Context
) : ListAdapter<ProfileListItem, ProfileHorizontalListAdapter.ProfileHorizontalListsViewHolder>(
    ProfileListItemDiffCallback()
) {

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ProfileHorizontalListsViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.profile_lists_item, parent, false)
        return ProfileHorizontalListsViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProfileHorizontalListsViewHolder, position: Int) {
        val item = getItem(position)

        holder.listName.text = item.title
        if (item.imageURL1 != "") {
            holder.firstListPoster.visibility = View.VISIBLE
            if (item.imageURL1 != null) Glide.with(holder.firstListPoster.context)
                .load("https://image.tmdb.org/t/p/w92${item.imageURL1}")
                .into(holder.firstListPoster)
            else Glide.with(holder.firstListPoster.context).load(R.drawable.error_404)
                .into(holder.firstListPoster)
            if (item.imageURL2 != "") {
                holder.secondListPoster.visibility = View.VISIBLE
                if (item.imageURL2 != null) Glide.with(holder.secondListPoster.context)
                    .load("https://image.tmdb.org/t/p/w92${item.imageURL2}")
                    .into(holder.secondListPoster)
                else Glide.with(holder.secondListPoster.context).load(R.drawable.error_404)
                    .into(holder.secondListPoster)
                if (item.imageURL3 != "") {
                    holder.thirdListPoster.visibility = View.VISIBLE
                    if (item.imageURL3 != null) Glide.with(holder.thirdListPoster.context)
                        .load("https://image.tmdb.org/t/p/w92${item.imageURL3}")
                        .into(holder.thirdListPoster)
                    else Glide.with(holder.thirdListPoster.context).load(R.drawable.error_404)
                        .into(holder.thirdListPoster)
                }
            }
            Glide.with(holder.secondListPoster.context)
                .load("https://image.tmdb.org/t/p/w92${item.imageURL2}")
                .into(holder.secondListPoster)
            Glide.with(holder.thirdListPoster.context)
                .load("https://image.tmdb.org/t/p/w92${item.imageURL3}")
                .into(holder.thirdListPoster)
        }

        holder.card.setOnClickListener {
            when (item.title) {
                "WATCHLIST (FILM)" -> {
                    val intent = Intent(context, ViewAllActivity::class.java)
                    intent.putExtra("entities", ArrayList(userLists["watchlist_m"] ?: emptyList()))
                    intent.putExtra("title", "Watchlist dei film")
                    intent.putExtra("type", 'm')
                    context.startActivity(intent)
                }

                "WATCHLIST (TV)" -> {
                    val intent = Intent(context, ViewAllActivity::class.java)
                    intent.putExtra("entities", ArrayList(userLists["watchlist_t"] ?: emptyList()))
                    intent.putExtra("title", "Watchlist delle serie TV")
                    intent.putExtra("type", 't')
                    context.startActivity(intent)
                }

                "VISTI (FILM)" -> {
                    val intent = Intent(context, ViewAllActivity::class.java)
                    intent.putExtra("entities", ArrayList(userLists["watched_m"] ?: emptyList()))
                    intent.putExtra("title", "Film visti")
                    intent.putExtra("type", 'm')
                    context.startActivity(intent)
                }

                "IN VISIONE (TV)" -> {
                    val intent = Intent(context, ViewAllActivity::class.java)
                    intent.putExtra("entities", ArrayList(userLists["watching_t"] ?: emptyList()))
                    intent.putExtra("title", "Serie TV in visione")
                    intent.putExtra("type", 't')
                    context.startActivity(intent)
                }

                "PREFERITI (FILM)" -> {
                    val intent = Intent(context, ViewAllActivity::class.java)
                    intent.putExtra("entities", ArrayList(userLists["favorite_m"] ?: emptyList()))
                    intent.putExtra("title", "Film preferiti")
                    intent.putExtra("type", 'm')
                    context.startActivity(intent)
                }

                "PREFERITI (TV)" -> {
                    val intent = Intent(context, ViewAllActivity::class.java)
                    intent.putExtra("entities", ArrayList(userLists["favorite_t"] ?: emptyList()))
                    intent.putExtra("title", "Serie TV preferite")
                    intent.putExtra("type", 't')
                    context.startActivity(intent)
                }
            }
        }
    }

    inner class ProfileHorizontalListsViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val card: MaterialCardView = itemView.findViewById(R.id.list_card)
        val listName: TextView = itemView.findViewById(R.id.list_name)
        val firstListPoster: ShapeableImageView = itemView.findViewById(R.id.first_list_poster)
        val secondListPoster: ShapeableImageView = itemView.findViewById(R.id.second_list_poster)
        val thirdListPoster: ShapeableImageView = itemView.findViewById(R.id.third_list_poster)
    }

    class ProfileListItemDiffCallback : DiffUtil.ItemCallback<ProfileListItem>() {
        override fun areItemsTheSame(oldItem: ProfileListItem, newItem: ProfileListItem): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(
            oldItem: ProfileListItem, newItem: ProfileListItem
        ): Boolean {
            return oldItem == newItem
        }
    }
}
