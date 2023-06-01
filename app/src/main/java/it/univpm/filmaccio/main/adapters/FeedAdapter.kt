package it.univpm.filmaccio.main.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import it.univpm.filmaccio.R

class FeedAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // Lista di dati da visualizzare
    var data = listOf<ActivityItem>()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemViewType(position: Int): Int {
        return data[position].type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return FollowViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is FollowViewHolder -> {
                val item = data[position] as ActivityItem.FollowActivityItem
                holder.bind(item)
            }

            is ReviewViewHolder -> {
                val item = data[position] as ActivityItem.ReviewActivityItem
                holder.bind(item)
            }
            // Aggiungere altri tipi di ViewHolder
        }
    }

    override fun getItemCount(): Int = data.size

    // ViewHolder per il feed di follow (già seguito)
    class FollowViewHolder private constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: ActivityItem.FollowActivityItem) {
            // Bisogna effettuare il binding dei dati con la view
        }

        companion object {
            fun from(parent: ViewGroup): FollowViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.already_follow_feed_item, parent, false)
                return FollowViewHolder(view)
            }
        }
    }

    // View Holder per il feed di recensioni
    class ReviewViewHolder private constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: ActivityItem.ReviewActivityItem) {
            // Bisgona effettuare il binding dei dati alle view
        }

        companion object {
            fun from(parent: ViewGroup): ReviewViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.review_feed_item, parent, false)
                return ReviewViewHolder(view)
            }
        }
    }

    // TODO: Aggiungere gli altri tipi di feed
}
