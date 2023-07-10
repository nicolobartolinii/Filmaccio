package it.univpm.filmaccio.main.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ViewFlipper
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import it.univpm.filmaccio.data.models.Movie
import it.univpm.filmaccio.data.models.Series
import it.univpm.filmaccio.data.models.TmdbEntity
import it.univpm.filmaccio.databinding.FragmentHomeBinding
import it.univpm.filmaccio.details.activities.MovieDetailsActivity
import it.univpm.filmaccio.details.activities.SeriesDetailsActivity
import it.univpm.filmaccio.main.activities.ViewAllActivity
import it.univpm.filmaccio.main.viewmodels.HomeViewModel

// Questo fragment è la schermata home dell'app. In questa schermata vengono mostrati i film attualmente in onda,
// un breve riassunto dei primi tre feed e dei primi tre episodi da vedere.
// Le informazioni su feed e episodi non sono ancora mostrate quindi le ho rimpiute con dei rettangoli placeholder.
// I film in onda invece vengono mostrati, senza però aver ancora implementato il pulsante "vedi tutti". Quindi
// si vedono solo i primi tre.

/**
 * Questo fragment è la schermata home dell'app. In questa schermata vengono mostrati i film attualmente in onda,
 * e le classifiche dei film e delle serie tv più votati in base alle valutazioni fornite dagli utenti
 * della nostra app.
 *
 * @author nicolobartolinii
 */
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // Questa è la prima classe in cui utilizziamo l'architettura MVVM. Il ViewModel è una classe che contiene
    // i dati che devono essere mostrati nella schermata.
    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    private lateinit var nowPlayingMoviesPosters: List<ShapeableImageView>
    private lateinit var topRatedMoviesPosters: List<ShapeableImageView>
    private lateinit var topRatedSeriesPosters: List<ShapeableImageView>
    private lateinit var viewFlipperHome: ViewFlipper

    private lateinit var latestReleases: List<Movie>
    private lateinit var topRatedMovies: List<Movie>
    private lateinit var topRatedSeries: List<Series>
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        nowPlayingMoviesPosters = listOf(
            binding.firstLatestReleaseHome,
            binding.secondLatestReleaseHome,
            binding.thirdLatestReleaseHome
        )
        topRatedMoviesPosters = listOf(
            binding.firstTopRatedMovieHome,
            binding.secondTopRatedMovieHome,
            binding.thirdTopRatedMovieHome
        )
        topRatedSeriesPosters = listOf(
            binding.firstTopRatedSeriesHome,
            binding.secondTopRatedSeriesHome,
            binding.thirdTopRatedSeriesHome
        )
        viewFlipperHome = binding.viewFlipperHome
        viewFlipperHome.displayedChild = 0
        // Qui creiamo una lista vuota che poi conterrà gli id dei film in onda.
        val nowPlayingMovieIds = mutableListOf(0L, 0L, 0L)
        // Qui osserviamo il LiveData che contiene i film in onda nel viewmodel.
        // Questa è una gestione asincrona dei dati. Si osserva la variabile nowPlayingMovies del viewModel
        // e quando questa cambia si esegue il codice che c'è dentro il blocco.
        homeViewModel.nowPlayingMovies.observe(viewLifecycleOwner) {
            latestReleases = it.movies
            // Quando la variabile del viewModel cambia (cioè quando il viewModel viene inizializzato, come si
            // può vedere nel file HomeViewModel.kt) eseguiamo un ciclo in cui andiamo ad aggiungere alla
            // lista movieIds gli id dei film in onda e andiamo a caricare le immagini dei poster dei film
            // nei riquadri della schermata home.
            for (i in 0..2) {
                nowPlayingMovieIds[i] = it.movies[i].id
                Glide.with(this).load("https://image.tmdb.org/t/p/w185${it.movies[i].posterPath}")
                    .into(nowPlayingMoviesPosters[i])
                // Un piccolo dettaglio su questa cosa è il link che viene utilizzato per caricare le immagini.
                // Questo link è standard fino a dopo il p/ dopo di che abbiamo la definizione della larghezza
                // dell'immagine da reperire da TMDB. Questa larghezza non è un numero a caso, infatti bisogna
                // controllare che il tipo di immagine che si sta ottenendo (poster, backdrop, etc.) sia
                // supportata dall'API. In questo caso prendiamo come larghezza 185 perché tra le varie larghezze supportate
                // per i poster credo che sia quella che più si avvicina a quello che ci serve in questa situazione.
                // Nella schermata di dettaglio dei film invece si utilizza una larghezza maggiore perché si vuole
                // che l'immagine sia più grande (w342).
            }
        }

        homeViewModel.topRatedMovies.observe(viewLifecycleOwner) {
            topRatedMovies = it
            // Qui si fa la stessa cosa che si fa per i film in onda, ma per i film più votati.
            for (i in 0..2) {
                Glide.with(this).load("https://image.tmdb.org/t/p/w185${it[i].posterPath}")
                    .into(topRatedMoviesPosters[i])
            }
        }

        homeViewModel.topRatedSeries.observe(viewLifecycleOwner) {
            topRatedSeries = it
            // Qui si fa la stessa cosa che si fa per i film in onda, ma per le serie tv più votate.
            for (i in 0..2) {
                Glide.with(this).load("https://image.tmdb.org/t/p/w185${it[i].posterPath}")
                    .into(topRatedSeriesPosters[i])
            }
            viewFlipperHome.displayedChild = 1
        }

        // Impostazione del listener sul click per il primo poster dei film in onda
        binding.firstLatestReleaseHome.setOnClickListener {
            // Quando viene cliccato il poster del film in onda, viene aperta la schermata di dettaglio del film corrispondente
            // aggiungendo all'intent l'id del film. in modo da poterlo recuperare nella schermata di dettaglio.
            val intent = Intent(context, MovieDetailsActivity::class.java)
            intent.putExtra("movieId", nowPlayingMovieIds[0])
            startActivity(intent)
        }

        // Impostazione del listener sul click per il secondo poster dei film in onda
        binding.secondLatestReleaseHome.setOnClickListener {
            val intent = Intent(context, MovieDetailsActivity::class.java)
            intent.putExtra("movieId", nowPlayingMovieIds[1])
            startActivity(intent)
        }

        // Impostazione del listener sul click per il terzo poster dei film in onda
        binding.thirdLatestReleaseHome.setOnClickListener {
            val intent = Intent(context, MovieDetailsActivity::class.java)
            intent.putExtra("movieId", nowPlayingMovieIds[2])
            startActivity(intent)
        }

        // Impostazione del listener sul click per il primo poster dei film più votati
        binding.firstTopRatedMovieHome.setOnClickListener {
            val intent = Intent(context, MovieDetailsActivity::class.java)
            intent.putExtra("movieId", topRatedMovies[0].id)
            startActivity(intent)
        }

        // Impostazione del listener sul click per il secondo poster dei film più votati
        binding.secondTopRatedMovieHome.setOnClickListener {
            val intent = Intent(context, MovieDetailsActivity::class.java)
            intent.putExtra("movieId", topRatedMovies[1].id)
            startActivity(intent)
        }

        // Impostazione del listener sul click per il terzo poster dei film più votati
        binding.thirdTopRatedMovieHome.setOnClickListener {
            val intent = Intent(context, MovieDetailsActivity::class.java)
            intent.putExtra("movieId", topRatedMovies[2].id)
            startActivity(intent)
        }

        // Impostazione del listener sul click per il primo poster delle serie tv più votate
        binding.firstTopRatedSeriesHome.setOnClickListener {
            val intent = Intent(context, SeriesDetailsActivity::class.java)
            intent.putExtra("seriesId", topRatedSeries[0].id)
            startActivity(intent)
        }

        // Impostazione del listener sul click per il secondo poster delle serie tv più votate
        binding.secondTopRatedSeriesHome.setOnClickListener {
            val intent = Intent(context, SeriesDetailsActivity::class.java)
            intent.putExtra("seriesId", topRatedSeries[1].id)
            startActivity(intent)
        }

        // Impostazione del listener sul click per il terzo poster delle serie tv più votate
        binding.thirdTopRatedSeriesHome.setOnClickListener {
            val intent = Intent(context, SeriesDetailsActivity::class.java)
            intent.putExtra("seriesId", topRatedSeries[2].id)
            startActivity(intent)
        }

        binding.buttonLatestReleasesViewAllHome.setOnClickListener {
            val intent = Intent(requireContext(), ViewAllActivity::class.java)
            intent.putExtra("entities", ArrayList(latestReleases)) // entities è la lista di entità
            intent.putExtra("title", "Ultime uscite") // title è il titolo della schermata
            startActivity(intent)
        }

        binding.buttonTopRatedMoviesViewAll.setOnClickListener {
            val intent = Intent(requireContext(), ViewAllActivity::class.java)
            intent.putExtra("entities", ArrayList(topRatedMovies.take(25)))
            intent.putExtra("title", "Film più votati (top 25)")
            startActivity(intent)
        }

        binding.buttonTopRatedSeriesViewAll.setOnClickListener {
            val tmdbEntitySeries = ArrayList<TmdbEntity>()
            for (series in topRatedSeries.take(25)) {
                tmdbEntitySeries.add(
                    TmdbEntity(
                        series.id,
                        series.title,
                        series.posterPath,
                        "series"
                    )
                )
            }
            val intent = Intent(requireContext(), ViewAllActivity::class.java)
            intent.putExtra("entities", tmdbEntitySeries)
            intent.putExtra("title", "Serie TV più votate (top 25)")
            startActivity(intent)
        }
    }
}