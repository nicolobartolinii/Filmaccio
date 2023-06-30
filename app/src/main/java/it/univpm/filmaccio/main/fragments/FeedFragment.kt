package it.univpm.filmaccio.main.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import it.univpm.filmaccio.databinding.FragmentFeedBinding
import it.univpm.filmaccio.main.viewmodels.FeedViewModel

// Questo fragment è la schermata in cui dovrebbe essere mostrato il feed di film, serie TV, persone
// e altri utenti all'utente. Per ora non è stato implementato nulla.
class FeedFragment : Fragment() {
    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!

    private val feedViewModel: FeedViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeedBinding
            .inflate(inflater, container, false)


        binding.submitButton.setOnClickListener {
            val n = binding.ratingBar.numStars
            val r = binding.ratingBar.rating
            val t = binding.ratingBar.stepSize
            Toast.makeText(
                requireContext(),
                "numStars: $n, rating: $r, stepSize: $t",
                Toast.LENGTH_SHORT
            ).show()
        }

        return binding.root
    }
}