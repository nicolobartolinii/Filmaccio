package it.univpm.filmaccio.main.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import it.univpm.filmaccio.databinding.FragmentHomeBinding
import it.univpm.filmaccio.main.viewmodels.HomeViewModel

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding
            .inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val moviePostersHome = listOf(
            _binding?.firstLatestReleaseHome,
            _binding?.secondLatestReleaseHome,
            _binding?.thirdLatestReleaseHome
        )
        homeViewModel.nowPlayingMovies.observe(viewLifecycleOwner) {
            for (i in 0..2) {
                Glide.with(this)
                    .load("https://image.tmdb.org/t/p/w185${it.movies[i].posterPath}")
                    .into(moviePostersHome[i]!!)
            }
        }
    }
}