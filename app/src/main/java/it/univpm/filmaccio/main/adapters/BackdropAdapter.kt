package it.univpm.filmaccio.main.adapters

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import it.univpm.filmaccio.R

/**
 * Questa classe è l'adapter che gestisce la RecyclerView presente nella ChangeBackdropActivity.
 * In breve, mostra le immagini di sfondo disponibili per il profilo in una griglia di due colonne.
 *
 * @param listener listener per la selezione di un'immagine di sfondo
 *
 * @author nicolobartolinii
 */
class BackdropAdapter(private val listener: ImageSelectionListener) :
    RecyclerView.Adapter<BackdropAdapter.ViewHolder>() {

    interface ImageSelectionListener {
        fun onImageSelected(position: Int)
        fun onImageDeselected()
    }

    private val backdropList = ArrayList<String>()
    private var selectedViewHolder: ViewHolder? = null
    var selectedPosition: Int = -1

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(newList: List<String>) {
        backdropList.clear()
        backdropList.addAll(newList)
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val backdropImage: ShapeableImageView = view.findViewById(R.id.backdropImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.backdrop_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val imageUrl = backdropList[position]

        Glide.with(holder.backdropImage.context).load(imageUrl).into(holder.backdropImage)

        holder.backdropImage.setOnClickListener {
            selectedViewHolder?.backdropImage?.strokeWidth = 0F
            holder.backdropImage.isSelected = !holder.backdropImage.isSelected
            if (holder.backdropImage.isSelected) {
                selectedViewHolder = holder
                selectedPosition = position
                val typedValue = TypedValue()
                val theme = holder.itemView.context.theme
                theme.resolveAttribute(
                    com.google.android.material.R.attr.colorTertiaryContainer, typedValue, true
                )
                val color = typedValue.data
                holder.backdropImage.strokeColor = ColorStateList.valueOf(color)
                holder.backdropImage.strokeWidth = 10F
                listener.onImageSelected(position)
            } else {
                holder.backdropImage.strokeWidth = 0F
                selectedViewHolder = null
                selectedPosition = -1
                listener.onImageDeselected()
            }
        }
    }

    override fun getItemCount(): Int {
        return backdropList.size
    }
}