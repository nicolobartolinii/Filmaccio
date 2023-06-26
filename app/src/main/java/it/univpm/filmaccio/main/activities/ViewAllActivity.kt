package it.univpm.filmaccio.main.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import it.univpm.filmaccio.databinding.ActivityViewAllBinding
import it.univpm.filmaccio.main.adapters.ViewAllAdapter

class ViewAllActivity : AppCompatActivity() {
    private lateinit var binding: ActivityViewAllBinding
    private lateinit var viewAllAdapter: ViewAllAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewAllBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Assumiamo che tu stia passando la lista di entità attraverso l'intento
        val entities: List<Any> = intent.getSerializableExtra("entities") as List<Any>

        binding.viewFlipperViewAll.displayedChild = 1

        viewAllAdapter = ViewAllAdapter()
        viewAllAdapter.submitList(entities)
        binding.viewAllRecyclerView.adapter = viewAllAdapter
    }
}
