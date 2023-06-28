package it.univpm.filmaccio.details.activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import it.univpm.filmaccio.R
import androidx.lifecycle.lifecycleScope
import com.google.android.material.card.MaterialCardView
import it.univpm.filmaccio.data.repository.MovieRepository
import it.univpm.filmaccio.data.repository.SeriesRepository
import it.univpm.filmaccio.databinding.ActivityUserDetailsBinding
import it.univpm.filmaccio.databinding.ActivityViewAllBinding
import it.univpm.filmaccio.main.utils.FirestoreService.followUser
import it.univpm.filmaccio.details.viewmodels.UserDetailsViewModel
import it.univpm.filmaccio.main.adapters.ProfileHorizontalListAdapter
import it.univpm.filmaccio.main.utils.FirestoreService
import it.univpm.filmaccio.main.utils.FirestoreService.getFollowers
import it.univpm.filmaccio.main.utils.FirestoreService.getFollowing
import it.univpm.filmaccio.main.utils.FirestoreService.getList
import it.univpm.filmaccio.main.utils.UserUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class UserDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserDetailsBinding

    private lateinit var userDetailsViewModel: UserDetailsViewModel
    private lateinit var profileImage: ShapeableImageView
    private lateinit var displayNameTextView: TextView
    private lateinit var usernameTextView: TextView
    private lateinit var backdropImageView: ShapeableImageView
    private lateinit var seguiBotton: Button
    private lateinit var followerTextView: TextView
    private lateinit var followingTextView: TextView
    private lateinit var followingCard: MaterialCardView
    private lateinit var followersCard: MaterialCardView
    // variabili per le repository
    private lateinit var movieRepository: MovieRepository
    private lateinit var seriesRepository: SeriesRepository
    // variabili per segnare il tempo
    private var movieMinutes = 0
    private var tvMinutes = 0
    private var movieNumber = 0
    private var tvNumber = 0
    private var followersNumber = 0
    private var followingNumber = 0


    // aapter
    private lateinit var profileListsAdapter: ProfileHorizontalListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val nameShown = intent.getStringExtra("nameShown")
        val username = intent.getStringExtra("username")
        val propic = intent.getStringExtra("profileImage")
        val backdropImage = intent.getStringExtra("backdropImage")
        seguiBotton = binding.buttonFollow
        displayNameTextView = binding.displayNameText
        usernameTextView = binding.usernameText
        profileImage = binding.profileImage
        backdropImageView=binding.backdropImage
        followerTextView=binding.followersNumber
        followingTextView=binding.followingNumber

        // Inizializzazione repo
        movieRepository = MovieRepository()
        seriesRepository = SeriesRepository()


        displayNameTextView.text = nameShown
        usernameTextView.text = username
        Glide.with(this).load(propic).into(profileImage)
        Glide.with(this).load(backdropImage).into(backdropImageView)
        val auth = UserUtils.auth
        val currentUserUid = auth.uid
        val targetUid = intent.getStringExtra("uid")!!


        val followersFlow = getFollowers(targetUid) // chiamata alla funzione che ritorna il numero di follower
        val watchedMoviesFlow = getList(targetUid, "watched_m") // chiamata alla funzione che ritorna il numero di film visti
        val followingFlow= getFollowing(targetUid) // chiamata alla funzione che ritorna il numero di following


        lifecycleScope.launch(Dispatchers.Main) {
            // funzione che mette nel textview il numero di follower
            followersFlow.collect { followers ->
                followerTextView.text = followers.size.toString()
            }
        }
        lifecycleScope.launch(Dispatchers.Main) {
            //funzione che mette nel textview il numero di film visti
            watchedMoviesFlow.collect { watchedMovies ->
                binding.moviesSeenNumber.text = watchedMovies.size.toString()
            }
        }
        lifecycleScope.launch(Dispatchers.Main) {
            //funzione che mette nel textview il numero di follower
            followingFlow.collect { following ->
                followingTextView.text = following.size.toString()
            }
        }

        // istanza del viewmodel
        userDetailsViewModel = ViewModelProvider(
            this,
            UserDetailsViewModel.UserDetailsViewModelFactory(targetUid)
        )[UserDetailsViewModel::class.java]

        userDetailsViewModel.isUserFollowed.observe(this) { isUserFollowed ->
            //obser vero o falso se l'utente è seguito o meno
            if (isUserFollowed == true) seguiBotton.text = "SEGUI GIÀ"
            else seguiBotton.text = "SEGUI"

        }
        seguiBotton.setOnClickListener {
            // funzione che implementa il segui stile instagram
            if (seguiBotton.text == "SEGUI") {
                FirestoreService.followUser(currentUserUid!!, targetUid)
                seguiBotton.text = "SEGUI GIA"
                lifecycleScope.launch(Dispatchers.Main) {
                    // funzione che mette nel textview il numero di follower
                    followersFlow.collect { followers ->
                        followerTextView.text = followers.size.toString()
                    }
                }
            } else {
                FirestoreService.unfollowUser(currentUserUid!!, targetUid)
                seguiBotton.text = "SEGUI"
                lifecycleScope.launch(Dispatchers.Main) {
                    // funzione che mette nel textview il numero di follower
                    lifecycleScope.launch(Dispatchers.Main) {
                        // funzione che mette nel textview il numero di follower
                        followersFlow.collect { followers ->
                            followerTextView.text = followers.size.toString()
                        }
                    }
                }
            }
        }

//profileListsAdapter = ProfileHorizontalListAdapter()
        // Qui lanciamo una coroutine per ottenere le liste dell'utente corrente
        CoroutineScope(Dispatchers.Main).launch {
            // Collezioniamo le liste dell'utente corrente contenute nella variabile lists del view model
            userDetailsViewModel.lists.collectLatest { lists ->
                if (lists != null) {
                    // Se la variabile lists non è vuota allora possiamo procedere con il creare
                    // la lista di ProfileListItem da passare all'adapter
                    val profileListItems = lists.flatMap { entry ->
                        // considerando che ogni lista è una coppia (titolo, lista di id), qui
                        // otteniamo il titolo e la lista di id in variabili separate
                        val listTitle = entry.key
                        val ids = entry.value

                        if (listTitle == "watched_m") {
                            // Se la lista è quella dei film visti allora aggiorniamo le variabili
                            // movieMinutes e movieNumber con i valori corretti
                            movieMinutes =
                                ids.sumOf { movieRepository.getMovieDetails(it).duration }
                            movieNumber = ids.size
                        } else if (listTitle == "watching_t") {
                            // Se la lista è quella delle serie viste allora aggiorniamo le variabili
                            // tvMinutes e tvNumber con i valori corretti
                            tvMinutes =
                                ids.sumOf { seriesRepository.getSeriesDetails(it).seasons.sumOf { season -> season.episodes.sumOf { episode -> episode.duration } } }
                            tvNumber =
                                ids.sumOf { seriesRepository.getSeriesDetails(it).seasons.sumOf { season -> season.episodes.size } }
                        }

                        val movieTime = convertMinutesToMonthsDaysHours(movieMinutes)
                        val tvTime = convertMinutesToMonthsDaysHours(tvMinutes)

                        binding.movieTimeMonths.text = movieTime.first
                        binding.movieTimeDays.text = movieTime.second
                        binding.movieTimeHours.text = movieTime.third
                        binding.tvTimeMonths.text = tvTime.first
                        binding.tvTimeDays.text = tvTime.second
                        binding.tvTimeHours.text = tvTime.third
                        binding.moviesSeenNumber.text = movieNumber.toString()
                        binding.episodesSeenNumber.text = tvNumber.toString()

                        if (movieTime.first == "01") {
                            binding.movieTimeMonthsText.text = "mese"
                        }
                        if (movieTime.second == "01") {
                            binding.movieTimeDaysText.text = "giorno"
                        }
                        if (movieTime.third == "01") {
                            binding.movieTimeHoursText.text = "ora"
                        }
                        if (tvTime.first == "01") {
                            binding.tvTimeMonthsText.text = "mese"
                        }
                        if (tvTime.second == "01") {
                            binding.tvTimeDaysText.text = "giorno"
                        }
                        if (tvTime.third == "01") {
                            binding.tvTimeHoursText.text = "ora"
                        }

                        if (ids.size >= 3) {
                            // Se la lista di id è lunga almeno 3 allora possiamo creare una lista
                            // di ProfileListItem con i primi 3 id della lista di id e il titolo
                            val id1 = ids[0]
                            val id2 = ids[1]
                            val id3 = ids[2]
                            // Qui controlliamo se il titolo della lista finisce con 'm' o 't' per
                            // capire se la lista è di film o di serie TV e chiamare il metodo giusto
                            if (listTitle.last() == 'm') listOf(
                                movieRepository.convertIdToProfileListItem(
                                    id1, id2, id3, listTitle
                                )
                            )
                            else listOf(
                                seriesRepository.convertIdToProfileListItem(
                                    id1, id2, id3, listTitle
                                )
                            )
                        } else {
                            // Se la lista di id è più corta di 3 allora creiamo una lista di id
                            // basata sui primi tre id ma usiamo il metodo getOrNull per impostare
                            // (con l'operatore elvis) a 0L gli id mancanti. Così poi nel metodo
                            // convertIdToProfileListItem possiamo gestire il caso in cui l'id sia
                            // 0L e non esista nessun film/serie con quell'id. Fare riferimento
                            // alle classi repository per questi metodi.
                            val id1 = ids.getOrNull(0) ?: 0L
                            val id2 = ids.getOrNull(1) ?: 0L
                            val id3 = ids.getOrNull(2) ?: 0L
                            if (listTitle.last() == 'm') listOf(
                                movieRepository.convertIdToProfileListItem(
                                    id1, id2, id3, listTitle
                                )
                            )
                            else listOf(
                                seriesRepository.convertIdToProfileListItem(
                                    id1, id2, id3, listTitle
                                )
                            )
                        }
                    }

                    // una volta creata la lista di ProfileListItem la passiamo all'adapter in modo che
                    // lui possa fare il resto e mostrare le liste nella recycler view
//                    profileListsAdapter.submitList(profileListItems)
//                    viewFlipper.displayedChild = 1
                }
            }
        }

//        binding.listeHorizontalList.adapter = profileListsAdapter
    }
    private fun convertMinutesToMonthsDaysHours(minutes: Int): Triple<String, String, String> {
        // Questo metodo converte i minuti in mesi, giorni e ore
        var months = (minutes / 43200).toString()
        var days = ((minutes % 43200) / 1440).toString()
        var hours = ((minutes % 1440) / 60).toString()
        if (months.length == 1) months = months.padStart(2, '0')
        if (days.length == 1) days = days.padStart(2, '0')
        if (hours.length == 1) hours = hours.padStart(2, '0')
        return Triple(months, days, hours)
    }
}