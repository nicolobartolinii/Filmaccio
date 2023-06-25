package it.univpm.filmaccio.main

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import it.univpm.filmaccio.R
import it.univpm.filmaccio.main.adapters.BackdropAdapter
import it.univpm.filmaccio.main.viewmodels.ChangeBackdropViewModel
import java.lang.Thread.sleep

class ChangeBackdropActivity : AppCompatActivity(), BackdropAdapter.ImageSelectionListener {

    private val changeBackdropViewModel: ChangeBackdropViewModel by viewModels()

    private lateinit var backdropAdapter: BackdropAdapter
    private lateinit var nextPageButton: Button
    private lateinit var previousPageButton: Button
    private lateinit var confirmButton: ExtendedFloatingActionButton
    private lateinit var infoButton: Button
    private lateinit var pagesTextView: TextView

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_backdrop)

        backdropAdapter = BackdropAdapter(this)

        infoButton = findViewById(R.id.button_info)
        nextPageButton = findViewById(R.id.button_next)
        previousPageButton = findViewById(R.id.button_before)
        confirmButton = findViewById(R.id.confirm_button)
        pagesTextView = findViewById(R.id.textView_page)

        nextPageButton.setOnClickListener {
            changeBackdropViewModel.loadNextPage()
            if (confirmButton.visibility == View.VISIBLE) {
                confirmButton.shrink()
                confirmButton.hide()
            }
            updateNavigation()
        }

        previousPageButton.setOnClickListener {
            changeBackdropViewModel.loadPreviousPage()
            if (confirmButton.visibility == View.VISIBLE) {
                confirmButton.shrink()
                confirmButton.hide()
            }
            updateNavigation()
        }

        infoButton.setOnClickListener {
            MaterialAlertDialogBuilder(this).setTitle("Info")
                .setMessage("In questa schermata puoi scegliere l'immagine di sfondo che preferisci per il tuo profilo. Puoi scorrere le pagine con i pulsanti in basso e confermare la scelta selezionando un'immagine e cliccando sul pulsante che comparirà in basso a destra.\n\nLe immagini mostrate sono quelle dei film e delle serie TV che hai aggiunto ai preferiti.")
                .setPositiveButton("Ok") { dialog, _ ->
                    dialog.dismiss()
                }.show()
        }

        findViewById<RecyclerView>(R.id.backdropRecyclerView).apply {
            layoutManager = GridLayoutManager(this@ChangeBackdropActivity, 2)
            adapter = backdropAdapter
        }

        confirmButton.setOnClickListener {
            // Calcolo l'indice globale dell'elemento selezionato basandomi sulla sua posizione nella pagina corrente,
            // il numero della pagina corrente e il numero di elementi per pagina
            val positionInCurrentPage =
                backdropAdapter.selectedPosition
            val globalSelectedIndex =
                ((changeBackdropViewModel.currentPage - 1) * changeBackdropViewModel.itemsPerPage) + positionInCurrentPage

            // Prendo l'URL dell'immagine dall'indice globale e lo passo all'activity chiamante
            val imageUrl = changeBackdropViewModel.backdropsTotal[globalSelectedIndex]
            val resultIntent = Intent().apply {
                putExtra("selectedImageUrl", imageUrl)
            }
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }

        changeBackdropViewModel.backdrops.observe(this) { backdrops ->
            backdropAdapter.submitList(backdrops)
            updateNavigation()
        }

        changeBackdropViewModel.currentPageLiveData.observe(this) {
            updateNavigation()
        }

        changeBackdropViewModel.pagesLiveData.observe(this) {
            updateNavigation()
        }
    }

    override fun onImageSelected(position: Int) {
        confirmButton.show()
        confirmButton.extend()
    }

    override fun onImageDeselected() {
        confirmButton.shrink()
        confirmButton.hide()
    }

    @SuppressLint("SetTextI18n")
    private fun updateNavigation() {
        previousPageButton.isEnabled = changeBackdropViewModel.currentPageLiveData.value != 1
        nextPageButton.isEnabled =
            changeBackdropViewModel.currentPageLiveData.value != changeBackdropViewModel.pagesLiveData.value
        pagesTextView.text =
            "Pagina ${changeBackdropViewModel.currentPageLiveData.value} / ${changeBackdropViewModel.pagesLiveData.value}"
    }
}
