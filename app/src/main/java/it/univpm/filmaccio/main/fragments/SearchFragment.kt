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

// Questa è la schermata di ricerca dell'app, che può essere vista anche come la schermata di esplorazione.
// In questa schermata inizialmente abbiamo una lista di film e serie TV consigliati e di tendenza.
// In alto, però, abbiamo una barra di ricerca che permette di cercare film, serie TV, persone e altri utenti.
class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val searchViewModel: SearchViewModel by viewModels()
    // Queste variabili ci servono per gestire al meglio la funzionalità di ricerca.
    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    // Questa variabile è un adapter che è una classe necessaria per gestire le RecyclerView.
    // Le RecyclerView sono delle liste che permettono di visualizzare una lista di elementi
    // in modo efficiente, senza dover caricare tutti gli elementi in memoria.
    // A livello tecnico, le RecyclerView caricano in memoria solo gli elementi che sono visibili
    // a schermo, e quando si scorre la lista, gli elementi che non sono più visibili vengono
    // distrutti e ricreati con i nuovi dati. L'adapter è la classe che permette di gestire
    // questo comportamento.
    private val adapter = SearchResultAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding
            .inflate(inflater, container, false)

        binding.searchView.setupWithSearchBar(binding.searchBar)
        // Alla recyclerView deve essere associato il proprio adapter e il proprio layout manager (questo
        // solo se non è stato impostato nel file XML).
        binding.searchRecyclerView.adapter = adapter
        binding.searchRecyclerView.layoutManager = LinearLayoutManager(context)

        // Qui aggiungiamo un listener che si accorge dei cambiamenti del testo nella barra di ricerca.
        binding.searchView.editText.addTextChangedListener(object : TextWatcher {
            // Quando il testo cambia, inizializziamo a null la variabile searchJob, che è un job
            // che ci permette di gestire la ricerca in modo asincrono.
            private var searchJob: Job? = null

            // Questo metodo viene chiamato DOPO che il testo è cambiato.
            override fun afterTextChanged(s: Editable?) {
                // Se il testo cambiato è vuoto, non facciamo nulla.
                if (s.isNullOrEmpty()) {
                    return
                }
                // Se il testo non è vuoto, cancelliamo il job precedente (se esiste) e ne creiamo
                // uno nuovo.
                searchJob?.cancel()
                searchJob = scope.launch {
                    // Nel nuovo job, aspettiamo un secondo prima di fare la ricerca. In questo
                    // modo, se l'utente continua a scrivere entro quel secondo non facciamo la
                    // ricerca, ma se l'utente smette di scrivere per un secondo sì.
                    delay(1000)
                    if (isActive) {
                        // Quando il secondo è passato, facciamo la ricerca attraverso il
                        // view model.
                        searchViewModel.search(s.toString())
                    }
                }
            }

            // Questi due metodi sono necessari da scrivere ma non li usiamo quindi sono vuoti.
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })


        // Qui osserviamo (come abbiamo fatto nella schermata home) la variabile del viewModel
        // che contiene i risultati della ricerca. Ogni volta che questa variabile cambia, viene
        // chiamato il metodo updateSearchResults dell'adapter, che aggiorna la lista di risultati
        // della ricerca.
        searchViewModel.searchResults.observe(viewLifecycleOwner) { results ->
            adapter.updateSearchResults(results)
        }

        return binding.root
    }

    // In questo metodo andiamo a inserire i poster dei film e delle serie TV consigliati e di
    // tendenza così come abbiamo fatto per i film in onda nella schermata Home.
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
        val recommendedMovieIds = mutableListOf(0L, 0L, 0L)
        val recommendedSeriesIds = mutableListOf(0L, 0L, 0L)
        val trendingMovieIds = mutableListOf(0L, 0L, 0L)
        val trendingSeriesIds = mutableListOf(0L, 0L, 0L)
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

    // Quando il fragment viene distrutto, cancelliamo il job che abbiamo creato per la ricerca in modo
    // da non farlo continuare in background e consumare risorse.
    override fun onDestroyView() {
        super.onDestroyView()
        job.cancel()
    }
}