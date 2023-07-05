package it.univpm.filmaccio.details.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import android.widget.ViewFlipper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import com.google.android.material.imageview.ShapeableImageView
import it.univpm.filmaccio.data.models.Series
import it.univpm.filmaccio.data.models.User
import it.univpm.filmaccio.data.repository.MovieRepository
import it.univpm.filmaccio.data.repository.SeriesRepository
import it.univpm.filmaccio.databinding.ActivityUserDetailsBinding
import it.univpm.filmaccio.details.viewmodels.UserDetailsViewModel
import it.univpm.filmaccio.main.activities.ViewAllActivity
import it.univpm.filmaccio.main.adapters.ProfileHorizontalListAdapter
import it.univpm.filmaccio.main.adapters.ViewAllAdapter
import it.univpm.filmaccio.main.utils.FirestoreService
import it.univpm.filmaccio.main.utils.FirestoreService.getFollowers
import it.univpm.filmaccio.main.utils.FirestoreService.getFollowing
import it.univpm.filmaccio.main.utils.UserUtils
import kotlinx.coroutines.Dispatchers
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
    private lateinit var viewFlipper: ViewFlipper
    private lateinit var finishedSeriesRecyclerView: RecyclerView
    private lateinit var finishedSeriesViewAllButton: Button
    private lateinit var finishedSeriesViewFlipper: ViewFlipper
    private lateinit var viewFlipperLists: ViewFlipper

    // variabili per le repository
    private lateinit var movieRepository: MovieRepository
    private lateinit var seriesRepository: SeriesRepository

    // variabili per segnare il tempo
    private var movieMinutes = 0
    private var tvMinutes = 0
    private var movieNumber = 0
    private var tvNumber = 0

    // array per view all
    private var followersArrayList: ArrayList<String> = arrayListOf()
    private var followingArrayList: ArrayList<String> = arrayListOf()
    private lateinit var userLists: Map<String, List<Long>>
    private lateinit var finishedSeries: List<Series>
    private lateinit var targetUid: String
    private lateinit var currentUser: User

    // aapter
    private lateinit var profileListsAdapter: ProfileHorizontalListAdapter

    @SuppressLint("SetTextI18n")
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
        backdropImageView = binding.backdropImage
        followerTextView = binding.followersNumber
        followingTextView = binding.followingNumber
        followingCard = binding.followingCard
        followersCard = binding.followersCard
        viewFlipper = binding.viewFlipper
        finishedSeriesRecyclerView = binding.serieTVHorizontalList
        finishedSeriesViewAllButton = binding.finishedSeriesButtonViewAll
        finishedSeriesViewFlipper = binding.finishedSeriesViewFlipper
        viewFlipperLists = binding.viewFlipperLists
        // Inizializzazione repo
        movieRepository = MovieRepository()
        seriesRepository = SeriesRepository()

        displayNameTextView.text = nameShown
        usernameTextView.text = username
        Glide.with(this).load(propic).into(profileImage)
        Glide.with(this).load(backdropImage).into(backdropImageView)
        val auth = UserUtils.auth
        val currentUserUid = auth.uid
        targetUid = intent.getStringExtra("uid")!!

        if (targetUid == currentUserUid) {
            seguiBotton.visibility = Button.GONE
        }

        val followersFlow =
            getFollowers(targetUid) // chiamata alla funzione che ritorna il numero di follower
        val followingFlow =
            getFollowing(targetUid) // chiamata alla funzione che ritorna il numero di following


        lifecycleScope.launch(Dispatchers.Main) {
            // funzione che mette nel textview il numero di follower
            followersFlow.collect { followers ->
                followerTextView.text = followers.size.toString()
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
            this, UserDetailsViewModel.UserDetailsViewModelFactory(targetUid)
        )[UserDetailsViewModel::class.java]

        // Qui controlliamo se il fragment sta venendo avviato per la prima volta dall'avvio dell'app
        if (userDetailsViewModel.isFirstLaunch) {
            // Se è la prima volta allora facciamo comparire la schermata di caricamento con un
            // breve ritardo di 25 secondi (per qualche motivo questa cosa serve solo al primo avvio)
            Handler(Looper.getMainLooper()).postDelayed({
                viewFlipper.displayedChild = 0
                viewFlipperLists.displayedChild = 0
            }, 25)
            userDetailsViewModel.isFirstLaunch = false
        }

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

        lifecycleScope.launch {
            //funzione per ottenere i following in array list per passarli successivamente
            followingFlow.collect { followingFlow ->
                followingArrayList = ArrayList(followingFlow)
            }
        }

        lifecycleScope.launch {
            //funzione per ottenere i followers in array list per passarli successivamente
            followersFlow.collect { followersFlow ->
                followersArrayList = ArrayList(followersFlow)
            }
        }

        followingCard.setOnClickListener {
            // clicco su following e parte il view all
            val intent = Intent(this, ViewAllActivity::class.java)
            intent.putExtra("entities", followingArrayList) // entities è la lista di entità
            intent.putExtra("title", "Seguiti") // title è il titolo della schermata
            startActivity(intent)
        }
        followersCard.setOnClickListener {
            // clicco su following e parte il view all
            val intent = Intent(this, ViewAllActivity::class.java)
            intent.putExtra("entities", followersArrayList) // entities è la lista di entità
            intent.putExtra("title", "Followers") // title è il titolo della schermata
            startActivity(intent)
        }

        lifecycleScope.launch {
            loadCurrentUserDetails()
        }

        lifecycleScope.launch {
            loadProfileListsAndTimes()
        }


        finishedSeriesViewAllButton.setOnClickListener {
            val intent = Intent(this, ViewAllActivity::class.java)
            intent.putExtra("entities", ArrayList(finishedSeries))
            intent.putExtra("title", "Serie TV completate")
            startActivity(intent)
        }
    }

    @SuppressLint("SetTextI18n")
    private suspend fun loadProfileListsAndTimes() {
        // Collezioniamo le liste dell'utente corrente contenute nella variabile lists del view model
        userDetailsViewModel.lists.collectLatest { lists ->
            if (lists != null) {
                userLists = lists
                // Se la variabile lists non è vuota allora possiamo procedere con il creare
                // la lista di ProfileListItem da passare all'adapter
                val profileListItems = lists.flatMap { entry ->
                    // considerando che ogni lista è una coppia (titolo, lista di id), qui
                    // otteniamo il titolo e la lista di id in variabili separate
                    val listTitle = entry.key
                    val ids = entry.value

                    if (listTitle == "finished_t") {
                        finishedSeries = ids.map { seriesRepository.getSeriesDetails(it) }
                        val firstThreeFinishedSeries = finishedSeries.take(3)
                        val adapter = ViewAllAdapter()
                        adapter.submitList(firstThreeFinishedSeries)
                        finishedSeriesRecyclerView.adapter = adapter
                    }

                    movieMinutes = currentUser.movieMinutes.toInt()
                    movieNumber = currentUser.moviesNumber.toInt()
                    tvMinutes = currentUser.tvMinutes.toInt()
                    tvNumber = currentUser.tvNumber.toInt()

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

                    viewFlipper.displayedChild = 1

                    if (ids.size >= 3) {
                        // Se la lista di id è lunga almeno 3 allora possiamo creare una lista
                        // di ProfileListItem con i primi 3 id della lista di id e il titolo
                        val id1 = ids[0]
                        val id2 = ids[1]
                        val id3 = ids[2]
                        // Qui controlliamo se il titolo della lista finisce con 'm' o 't' per
                        // capire se la lista è di film o di serie TV e chiamare il metodo giusto
                        if (listTitle == "finished_t") {
                            listOf()
                        } else if (listTitle.last() == 'm') listOf(
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
                        if (listTitle == "finished_t") {
                            listOf()
                        } else if (listTitle.last() == 'm') listOf(
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
                profileListsAdapter = ProfileHorizontalListAdapter(userLists, this)
                binding.horizontalCardsLists.adapter = profileListsAdapter
                profileListsAdapter.submitList(profileListItems)
                viewFlipperLists.displayedChild = 1
                if (finishedSeriesRecyclerView.adapter?.itemCount == 0) {
                    finishedSeriesViewFlipper.displayedChild = 1
                } else finishedSeriesViewFlipper.displayedChild = 0
            }
        }
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

    private suspend fun loadCurrentUserDetails() {
        userDetailsViewModel.currentUser.collectLatest {
            if (it != null) currentUser = it
        }
    }
}