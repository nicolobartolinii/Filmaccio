package it.univpm.filmaccio.main.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import it.univpm.filmaccio.R
import it.univpm.filmaccio.databinding.FragmentSearchBinding
import it.univpm.filmaccio.main.viewmodels.SearchViewModel

class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val searchViewModel: SearchViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding
            .inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recommendedMoviePosters = listOf(
            _binding?.firstRecommendedMovieSearch,
            _binding?.secondRecommendedMovieSearch,
            _binding?.thirdRecommendedMovieSearch
        )
        val recommendedSeriesPosters = listOf(
            _binding?.firstRecommendedSeriesSearch,
            _binding?.secondRecommendedSeriesSearch,
            _binding?.thirdRecommendedSeriesSearch
        )
        val trendingMoviePosters = listOf(
            _binding?.firstTrendingMovieSearch,
            _binding?.secondTrendingMovieSearch,
            _binding?.thirdTrendingMovieSearch
        )
        val trendingSeriesPosters = listOf(
            _binding?.firstTrendingSeriesSearch,
            _binding?.secondTrendingSeriesSearch,
            _binding?.thirdTrendingSeriesSearch
        )
        searchViewModel.topRatedMovies.observe(viewLifecycleOwner) {
            for (i in 0..2) {
                Glide.with(this)
                    .load("https://image.tmdb.org/t/p/w185${it.movies[i].posterPath}")
                    .into(recommendedMoviePosters[i]!!)
            }
        }
        searchViewModel.topRatedSeries.observe(viewLifecycleOwner) {
            for (i in 0..2) {
                Glide.with(this)
                    .load("https://image.tmdb.org/t/p/w185${it.series[i].posterPath}")
                    .into(recommendedSeriesPosters[i]!!)
            }
        }
        searchViewModel.trendingMovies.observe(viewLifecycleOwner) {
            for (i in 0..2) {
                Glide.with(this)
                    .load("https://image.tmdb.org/t/p/w185${it.movies[i].posterPath}")
                    .into(trendingMoviePosters[i]!!)
            }
        }
        searchViewModel.trendingSeries.observe(viewLifecycleOwner) {
            for (i in 0..2) {
                Glide.with(this)
                    .load("https://image.tmdb.org/t/p/w185${it.series[i].posterPath}")
                    .into(trendingSeriesPosters[i]!!)
            }
        }
    }
}