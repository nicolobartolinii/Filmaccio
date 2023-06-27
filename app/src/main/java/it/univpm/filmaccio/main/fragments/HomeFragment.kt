package it.univpm.filmaccio.main.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ViewFlipper
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import it.univpm.filmaccio.data.models.Movie
import it.univpm.filmaccio.databinding.FragmentHomeBinding
import it.univpm.filmaccio.details.activities.MovieDetailsActivity
import it.univpm.filmaccio.main.activities.ViewAllActivity
import it.univpm.filmaccio.main.utils.FirestoreService
import it.univpm.filmaccio.main.utils.UserUtils
import it.univpm.filmaccio.main.viewmodels.HomeViewModel

// Questo fragment è la schermata home dell'app. In questa schermata vengono mostrati i film attualmente in onda,
// un breve riassunto dei primi tre feed e dei primi tre episodi da vedere.
// Le informazioni su feed e episodi non sono ancora mostrate quindi le ho rimpiute con dei rettangoli placeholder.
// I film in onda invece vengono mostrati, senza però aver ancora implementato il pulsante "vedi tutti". Quindi
// si vedono solo i primi tre.
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // Questa è la prima classe in cui utilizziamo l'architettura MVVM. Il ViewModel è una classe che contiene
    // i dati che devono essere mostrati nella schermata.
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

    private lateinit var moviePostersHome: List<ImageView>
    private lateinit var viewFlipperHome: ViewFlipper

    private lateinit var latestReleases: List<Movie>
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        moviePostersHome = listOf(
            binding.firstLatestReleaseHome,
            binding.secondLatestReleaseHome,
            binding.thirdLatestReleaseHome
        )
        viewFlipperHome = binding.viewFlipperHome
        viewFlipperHome.displayedChild = 0
        // Qui creiamo una lista vuota che poi conterrà gli id dei film in onda.
        val movieIds = mutableListOf(0L, 0L, 0L)
        // Qui osserviamo il LiveData che contiene i film in onda nel viewmodel.
        // Questa è una gestione asincrona dei dati. Si osserva la variabile nowPlayingMovies del viewModel
        // e quando questa cambia si esegue il codice che c'è dentro il blocco di codice.
        homeViewModel.nowPlayingMovies.observe(viewLifecycleOwner) {
            latestReleases = it.movies
            // Quando la variabile del viewModel cambia (cioè quando il viewModel viene inizializzato, come si
            // può vedere nel file HomeViewModel.kt) eseguiamo un ciclo in cui andiamo ad aggiungere alla
            // lista movieIds gli id dei film in onda e andiamo a caricare le immagini dei poster dei film
            // nei riquadri della schermata home.
            for (i in 0..2) {
                movieIds[i] = it.movies[i].id
                Glide.with(this)
                    .load("https://image.tmdb.org/t/p/w185${it.movies[i].posterPath}")
                    .into(moviePostersHome[i])
                // Un piccolo dettaglio su questa cosa è il link che viene utilizzato per caricare le immagini.
                // Questo link è standard fino a dopo il p/ dopo di che abbiamo la definizione della larghezza
                // dell'immagine da reperire da TMDB. Questa larghezza non è un numero a caso, infatti bisogna
                // controllare che il tipo di immagine che si sta ottenendo (poster, backdrop, etc.) sia
                // supportata dall'API. In questo caso prendo come larghezza 185 perché tra le larghezze supportate
                // per i poster credo che sia quella che più si avvicina a quello che ci serve in questa situazione.
                // Nella schermata di dettaglio dei film invece si utilizza una larghezza maggiore perché si vuole
                // che l'immagine sia più grande (infatti uso w342 se non sbaglio).
            }
            viewFlipperHome.displayedChild = 1
        }

        // Impostazione del listener sul click per il primo poster dei film in onda
        binding.firstLatestReleaseHome.setOnClickListener {
            // Quando viene cliccato il poster del film in onda, viene aperta la schermata di dettaglio del film corrispondente
            // aggiungendo all'intent l'id del film. in modo da poterlo recuperare nella schermata di dettaglio.
            val intent = Intent(context, MovieDetailsActivity::class.java)
            intent.putExtra("movieId", movieIds[0])
            startActivity(intent)
        }

        // Impostazione del listener sul click per il secondo poster dei film in onda
        binding.secondLatestReleaseHome.setOnClickListener {
            val intent = Intent(context, MovieDetailsActivity::class.java)
            intent.putExtra("movieId", movieIds[1])
            startActivity(intent)
        }

        // Impostazione del listener sul click per il terzo poster dei film in onda
        binding.thirdLatestReleaseHome.setOnClickListener {
            val intent = Intent(context, MovieDetailsActivity::class.java)
            intent.putExtra("movieId", movieIds[2])
            startActivity(intent)
        }

        binding.buttonLatestReleasesViewAllHome.setOnClickListener {
            val intent = Intent(requireContext(), ViewAllActivity::class.java)
            intent.putExtra("entities", ArrayList(latestReleases)) // entities è la lista di entità
            intent.putExtra("title", "Ultime uscite") // title è il titolo della schermata
            startActivity(intent)
        }


        binding.buttonFeedViewAllHome.setOnClickListener {
            FirestoreService.addSeriesToWatching(UserUtils.getCurrentUserUid()!!, 4235)
        }
    }
}