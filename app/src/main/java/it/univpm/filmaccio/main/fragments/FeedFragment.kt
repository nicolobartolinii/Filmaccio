package it.univpm.filmaccio.main.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import it.univpm.filmaccio.databinding.FragmentFeedBinding
import it.univpm.filmaccio.main.viewmodels.FeedViewModel

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

        return binding.root
    }
}