package it.univpm.filmaccio.main.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import it.univpm.filmaccio.databinding.ActivityViewAllBinding
import it.univpm.filmaccio.main.adapters.ViewAllAdapter

/**
 * Questa classe è l'activity che gestisce la visualizzazione di una lista di entità qualsiasi.
 * È estremamente flessibile perché può visualizzare una lista di entità di moltissimi tipi: film, serie
 * TV, persone, utenti, recensioni.
 *
 * @author nicolobartolinii
 */
@Suppress("UNCHECKED_CAST", "DEPRECATION")
class ViewAllActivity : AppCompatActivity() {
    private lateinit var binding: ActivityViewAllBinding
    private lateinit var viewAllAdapter: ViewAllAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewAllBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewFlipperViewAll.displayedChild = 0

        val entities: List<Any> = intent.getSerializableExtra("entities") as List<Any>
        val title = intent.getStringExtra("title")
        val type = intent.getCharExtra("type", 'm')

        binding.viewAllTextView.text = title

        binding.viewFlipperViewAll.displayedChild = 1

        viewAllAdapter = ViewAllAdapter(type)
        viewAllAdapter.submitList(entities)
        binding.viewAllRecyclerView.adapter = viewAllAdapter

        if (entities.isEmpty()) {
            binding.viewAllRecyclerViewFlipper.displayedChild = 1
        } else {
            binding.viewAllRecyclerViewFlipper.displayedChild = 0
        }

        binding.buttonBack.setOnClickListener {
            finish()
        }
    }
}
