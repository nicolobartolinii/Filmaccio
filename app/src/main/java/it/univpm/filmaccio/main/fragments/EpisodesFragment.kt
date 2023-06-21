package it.univpm.filmaccio.main.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import it.univpm.filmaccio.databinding.FragmentEpisodesBinding
import it.univpm.filmaccio.main.viewmodels.EpisodesViewModel

// Questo fragment è la schermata in cui dovrebbero venir mostrati gli episodi delle serie TV che l'utente
// deve vedere. Per ora non è stato implementato nulla.
class EpisodesFragment : Fragment() {
    private var _binding: FragmentEpisodesBinding? = null
    private val binding get() = _binding!!

    private val episodesViewModel: EpisodesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEpisodesBinding
            .inflate(inflater, container, false)

        return binding.root
    }
}