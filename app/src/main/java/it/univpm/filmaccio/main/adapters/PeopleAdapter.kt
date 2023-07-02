package it.univpm.filmaccio.main.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
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
import it.univpm.filmaccio.data.models.NextEpisode
import it.univpm.filmaccio.data.models.Person
import it.univpm.filmaccio.data.repository.PeopleRepository
import it.univpm.filmaccio.data.repository.SeriesRepository
import it.univpm.filmaccio.details.activities.PersonDetailsActivity
import it.univpm.filmaccio.details.activities.SeriesDetailsActivity
import it.univpm.filmaccio.main.utils.FirestoreService
import it.univpm.filmaccio.main.utils.UserUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PeopleAdapter(
    private val followedPeople: List<Person>,
    private val context: Context
) : RecyclerView.Adapter<PeopleAdapter.PeopleViewHolder>() {

    private val peopleRepository = PeopleRepository()

    class PeopleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val personImage: ShapeableImageView = view.findViewById(R.id.followed_person_image)
        val personName: TextView = view.findViewById(R.id.followed_person_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeopleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.person_item, parent, false)
        return PeopleViewHolder(view)
    }

    override fun onBindViewHolder(holder: PeopleViewHolder, position: Int) {
        val person = followedPeople[position]
        if (person.profilePath != null) {
            Glide.with(context)
                .load("https://image.tmdb.org/t/p/w500${person.profilePath}")
                .into(holder.personImage)
        } else {
            Glide.with(context)
                .load(R.drawable.error_404)
                .into(holder.personImage)
        }
        holder.personName.text = person.name

        holder.itemView.setOnClickListener {
            val intent = Intent(context, PersonDetailsActivity::class.java)
            intent.putExtra("personId", person.id)
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = followedPeople.size
}