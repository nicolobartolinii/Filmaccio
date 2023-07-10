package it.univpm.filmaccio.main.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ViewFlipper
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import it.univpm.filmaccio.databinding.FragmentEpisodesBinding
import it.univpm.filmaccio.main.adapters.NextEpisodesAdapter
import it.univpm.filmaccio.main.viewmodels.NextEpisodesViewModel
import kotlinx.coroutines.launch

/**
 * Fragment che mostra gli episodi delle serie TV che l'utente deve vedere.
 *
 * @author nicolobartolinii
 */
class EpisodesFragment : Fragment() {
    private var _binding: FragmentEpisodesBinding? = null
    private val binding get() = _binding!!

    private val nextEpisodesViewModel: NextEpisodesViewModel by viewModels()

    private lateinit var buttonReload: Button
    private lateinit var buttonInfo: Button
    private lateinit var viewFlipper: ViewFlipper
    private lateinit var episodesList: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEpisodesBinding.inflate(inflater, container, false)

        buttonReload = binding.buttonReload
        buttonInfo = binding.buttonInfo
        viewFlipper = binding.innerViewFlipperEpisodes
        episodesList = binding.episodesRecyclerView

        viewFlipper.displayedChild = 0

        buttonReload.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                nextEpisodesViewModel.loadNextEpisodes()
            }
            viewFlipper.displayedChild = 0
        }

        buttonInfo.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext()).setTitle("Info").setMessage(
                "In questa schermata puoi tracciare gli episodi delle serie TV che stai guardando.\n" + "Quando aggiungi una serie TV alla lista delle serie TV in visione, in questa schermata comparirà il primo episodio, nella lista degli episodi della serie TV, che devi vedere.\n" + "Attraverso la schermata di dettaglio della serie TV (accessibile tramite la schermata di ricerca o cliccando sul nome della serie TV in questa schermata), puoi visualizzare tutti gli episodi della serie TV stessa e puoi segnarli come visti o meno. Questa cosa puoi farla direttamente anche in questa schermata, ma solo per un episodio alla volta.\n\n" + "N.B.: eventuali episodi \"Speciali\" non vengono considerati."
            ).setPositiveButton("Ok") { dialog, _ ->
                dialog.dismiss()
            }.show()
        }

        nextEpisodesViewModel.nextEpisodes.observe(viewLifecycleOwner) {
            if (it == null) {
                viewFlipper.displayedChild = 0
            } else if (it.isEmpty()) {
                viewFlipper.displayedChild = 2
            } else {
                viewFlipper.displayedChild = 1
                episodesList.adapter = NextEpisodesAdapter(it, requireContext())
            }
        }

        return binding.root
    }
}