package it.univpm.filmaccio.main.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ViewFlipper
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import it.univpm.filmaccio.data.models.NextEpisode
import it.univpm.filmaccio.databinding.FragmentEpisodesBinding
import it.univpm.filmaccio.main.adapters.NextEpisodesAdapter
import it.univpm.filmaccio.main.viewmodels.NextEpisodesViewModel

// Questo fragment è la schermata in cui dovrebbero venir mostrati gli episodi delle serie TV che l'utente
// deve vedere. Per ora non è stato implementato nulla.
class EpisodesFragment : Fragment() {
    private var _binding: FragmentEpisodesBinding? = null
    private val binding get() = _binding!!

    private val nextEpisodesViewModel: NextEpisodesViewModel by viewModels()

    private lateinit var buttonInfo: Button
    private lateinit var viewFlipper: ViewFlipper
    private lateinit var episodesList: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEpisodesBinding
            .inflate(inflater, container, false)

        buttonInfo = binding.buttonInfo
        viewFlipper = binding.innerViewFlipperEpisodes
        episodesList = binding.episodesRecyclerView

        buttonInfo.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext()).setTitle("Info")
                .setMessage("In questa schermata puoi tracciare gli episodi delle serie TV che stai guardando.\n" +
                        "Quando aggiungi una serie TV alla lista delle serie TV in visione, in questa schermata comparirà il primo episodio, nella lista degli episodi della serie TV, che devi vedere.\n" +
                        "Attraverso la schermata di dettaglio della serie TV (accessibile tramite la schermata di ricerca o cliccando sul nome della serie TV in questa schermata), puoi visualizzare tutti gli episodi della serie TV stessa e puoi segnarli come visti o meno. Questa cosa puoi farla direttamente anche in questa schermata, ma solo per un episodio alla volta.")
                .setPositiveButton("Ok") { dialog, _ ->
                    dialog.dismiss()
                }.show()
        }

        nextEpisodesViewModel.nextEpisodes.observe(viewLifecycleOwner) {
            if (it.isNullOrEmpty()) {
                viewFlipper.displayedChild = 0
            }
            else {
                viewFlipper.displayedChild = 1
                episodesList.adapter = NextEpisodesAdapter(it, requireContext())
            }
        }

        return binding.root
    }
}