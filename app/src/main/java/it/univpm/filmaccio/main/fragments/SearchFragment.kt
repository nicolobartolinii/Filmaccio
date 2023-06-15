package it.univpm.filmaccio.main.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import it.univpm.filmaccio.databinding.FragmentSearchBinding
import it.univpm.filmaccio.details.activities.MovieDetailsActivity
import it.univpm.filmaccio.details.activities.SeriesDetailsActivity
import it.univpm.filmaccio.main.adapters.SearchResultAdapter
import it.univpm.filmaccio.main.viewmodels.SearchViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val searchViewModel: SearchViewModel by viewModels()
    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.IO + job)
    private val adapter = SearchResultAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding
            .inflate(inflater, container, false)

        binding.searchView.setupWithSearchBar(binding.searchBar)
        binding.searchRecyclerView.adapter = adapter
        binding.searchRecyclerView.layoutManager = LinearLayoutManager(context)

        binding.searchView.editText.addTextChangedListener(object : TextWatcher {
            private var searchJob: Job? = null

            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    return
                }
                searchJob?.cancel()
                searchJob = scope.launch {
                    Log.e("Search", "Searching for $s in fragment")
                    delay(1000)
                    Log.e("Search", "Searching for $s in fragment after delay")
                    if (isActive) {
                        searchViewModel.search(s.toString())
                        Log.e("Search", "Searching for $s in fragment after delay and isActive")
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })


        searchViewModel.searchResults.observe(viewLifecycleOwner) { results ->
            Log.e("Search", "Updating search results")
            adapter.updateSearchResults(results)
        }

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
        val recommendedMovieIds = mutableListOf(0, 0, 0)
        val recommendedSeriesIds = mutableListOf(0, 0, 0)
        val trendingMovieIds = mutableListOf(0, 0, 0)
        val trendingSeriesIds = mutableListOf(0, 0, 0)
        searchViewModel.topRatedMovies.observe(viewLifecycleOwner) {
            for (i in 0..2) {
                recommendedMovieIds[i] = it.movies[i].id
                Glide.with(this)
                    .load("https://image.tmdb.org/t/p/w185${it.movies[i].posterPath}")
                    .into(recommendedMoviePosters[i]!!)
            }
        }
        searchViewModel.topRatedSeries.observe(viewLifecycleOwner) {
            for (i in 0..2) {
                recommendedSeriesIds[i] = it.series[i].id
                Glide.with(this)
                    .load("https://image.tmdb.org/t/p/w185${it.series[i].posterPath}")
                    .into(recommendedSeriesPosters[i]!!)
            }
        }
        searchViewModel.trendingMovies.observe(viewLifecycleOwner) {
            for (i in 0..2) {
                trendingMovieIds[i] = it.movies[i].id
                Glide.with(this)
                    .load("https://image.tmdb.org/t/p/w185${it.movies[i].posterPath}")
                    .into(trendingMoviePosters[i]!!)
            }
        }
        searchViewModel.trendingSeries.observe(viewLifecycleOwner) {
            for (i in 0..2) {
                trendingSeriesIds[i] = it.series[i].id
                Glide.with(this)
                    .load("https://image.tmdb.org/t/p/w185${it.series[i].posterPath}")
                    .into(trendingSeriesPosters[i]!!)
            }
        }

        for (poster in recommendedMoviePosters) {
            poster?.setOnClickListener {
                val intent = Intent(context, MovieDetailsActivity::class.java)
                intent.putExtra(
                    "movieId",
                    recommendedMovieIds[recommendedMoviePosters.indexOf(poster)]
                )
                startActivity(intent)
            }
        }

        for (poster in recommendedSeriesPosters) {
            poster?.setOnClickListener {
                val intent = Intent(context, SeriesDetailsActivity::class.java)
                intent.putExtra(
                    "seriesId",
                    recommendedSeriesIds[recommendedSeriesPosters.indexOf(poster)]
                )
                startActivity(intent)
            }
        }

        for (poster in trendingMoviePosters) {
            poster?.setOnClickListener {
                val intent = Intent(context, MovieDetailsActivity::class.java)
                intent.putExtra("movieId", trendingMovieIds[trendingMoviePosters.indexOf(poster)])
                startActivity(intent)
            }
        }

        for (poster in trendingSeriesPosters) {
            poster?.setOnClickListener {
                val intent = Intent(context, SeriesDetailsActivity::class.java)
                intent.putExtra(
                    "seriesId",
                    trendingSeriesIds[trendingSeriesPosters.indexOf(poster)]
                )
                startActivity(intent)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        job.cancel()
    }
}