package it.univpm.filmaccio.main.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.ViewFlipper
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import com.google.android.material.imageview.ShapeableImageView
import it.univpm.filmaccio.data.models.ProfileListItem
import it.univpm.filmaccio.data.models.Series
import it.univpm.filmaccio.data.models.User
import it.univpm.filmaccio.data.repository.MovieRepository
import it.univpm.filmaccio.data.repository.SeriesRepository
import it.univpm.filmaccio.databinding.FragmentProfileBinding
import it.univpm.filmaccio.main.activities.EditProfileActivity
import it.univpm.filmaccio.main.activities.SettingsActivity
import it.univpm.filmaccio.main.activities.ViewAllActivity
import it.univpm.filmaccio.main.adapters.ProfileHorizontalListAdapter
import it.univpm.filmaccio.main.adapters.ViewAllAdapter
import it.univpm.filmaccio.main.utils.FirestoreService
import it.univpm.filmaccio.main.utils.UserUtils
import it.univpm.filmaccio.main.viewmodels.ProfileViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// Questo fragment è la schermata in cui viene mostrato il profilo dell'utente corrente (non degli altri utenti).
// In questa schermata abbiamo in alto il backdrop scelto dall'utente (per ora è un placeholder)
// e sotto abbiamo: la foto profilo dell'utente, il nome visualizzato, lo username, le informazioni sui film/serie
// e sui followers/following e poi le liste di film/serie dell'utente.
// Per fare quell'effetto a scorrimento fixed del backdrop il file XML di questa schermata è molto molto
// contorto perché è stato un casino farlo bene, ma alla fine è venuto fuori un buon risultato quindi ci sta.
// Per il resto questo fragment ancora non è del tutto implementato, mancano alcuni dettagli e ci saranno
// sicuramente dei bug da risolvere.
class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var reloadButton: Button // bottone per ricaricare la pagina
    private lateinit var editProfileButton: Button // bottone per la pagina di modifica
    private lateinit var settingsButton: Button // bottone per le impostazioni
    private lateinit var viewFlipper: ViewFlipper
    private lateinit var profileImage: ShapeableImageView
    private lateinit var backdropImage: ImageView
    private lateinit var currentUser: User
    private lateinit var followerTextView: TextView
    private lateinit var followingTextView: TextView
    private lateinit var finishedSeriesRecyclerView: RecyclerView
    private lateinit var finishedSeriesViewAllButton: Button
    private lateinit var finishedSeriesViewFlipper: ViewFlipper
    private var movieMinutes = 0
    private var tvMinutes = 0
    private var movieNumber = 0
    private var tvNumber = 0
    private lateinit var followingCard: MaterialCardView
    private lateinit var followersCard: MaterialCardView
    private var followersArrayList: ArrayList<String> = arrayListOf()
    private var followingArrayList: ArrayList<String> = arrayListOf()
    private lateinit var finishedSeries: List<Series>
    private lateinit var userLists: Map<String, List<Long>>


    private val currentUserUid = UserUtils.getCurrentUserUid()!!

    private val profileViewModel: ProfileViewModel by viewModels()

    // Qui ci riferiamo ai repository per poter convertire le liste in ProfileListItem
    private val movieRepository = MovieRepository()
    private val seriesRepository = SeriesRepository()

    // Anche qui abbiamo un adapter per la recycler view delle liste
    private lateinit var profileListsAdapter: ProfileHorizontalListAdapter

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        // dichiarazione bottoni
        reloadButton = binding.reloadButton
        settingsButton = binding.settingsButton
        editProfileButton = binding.modifyProfileButton
        viewFlipper = binding.viewFlipper
        profileImage = binding.profileImage
        backdropImage = binding.backdropImage
        followerTextView = binding.followersNumber
        followingTextView = binding.followingNumber
        followingCard = binding.followingCard
        followersCard = binding.followersCard
        finishedSeriesRecyclerView = binding.serieTVHorizontalList
        finishedSeriesViewAllButton = binding.finishedSeriesButtonViewAll
        finishedSeriesViewFlipper = binding.finishedSeriesViewFlipper
        val followersFlow = FirestoreService.getFollowers(currentUserUid)
        val followingFlow = FirestoreService.getFollowing(currentUserUid)

        viewLifecycleOwner.lifecycleScope.launch {
            //funzione per ottenere i followers
            followersFlow.collect { followers ->
                followerTextView.text = followers.size.toString()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            //funzione per ottenere i following
            followingFlow.collect { following ->
                followingTextView.text = following.size.toString()
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            //funzione per ottenere i following in array list per passarli successivamente
            followingFlow.collect { followingFlow ->
                followingArrayList = ArrayList(followingFlow)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            //funzione per ottenere i follower  in array list per passarli successivamente
            followersFlow.collect { followersFlow ->
                followersArrayList = ArrayList(followersFlow)
            }
        }

        followingCard.setOnClickListener {
            // clicco su following e parte il view all
            val intent = Intent(requireContext(), ViewAllActivity::class.java)
            intent.putExtra("entities", followingArrayList) // entities è la lista di entità
            intent.putExtra("title", "Seguiti") // title è il titolo della schermata
            startActivity(intent)
        }

        followersCard.setOnClickListener {
            val intent = Intent(requireContext(), ViewAllActivity::class.java)
            intent.putExtra("entities", followersArrayList) // entities è la lista di entità
            intent.putExtra("title", "Followers") // title è il titolo della schermata
            startActivity(intent)
        }

        // Qui controlliamo se il fragment sta venendo avviato per la prima volta dall'avvio dell'app
        if (profileViewModel.isFirstLaunch) {
            // Se è la prima volta allora facciamo comparire la schermata di caricamento con un
            // breve ritardo di 25 secondi (per qualche motivo questa cosa serve solo al primo avvio)
            Handler(Looper.getMainLooper()).postDelayed({
                viewFlipper.displayedChild = 0
            }, 25)
            profileViewModel.isFirstLaunch = false
        }

        settingsButton.setOnClickListener {
            val intent = Intent(requireContext(), SettingsActivity::class.java)
            startActivity(intent)
        }

        editProfileButton.setOnClickListener {
            val intent = Intent(requireContext(), EditProfileActivity::class.java)
            intent.putExtra("uid", currentUser.uid)
            intent.putExtra("nameShown", currentUser.nameShown)
            intent.putExtra("email", currentUser.email)
            intent.putExtra("propic", currentUser.profileImage)
            intent.putExtra("backdrop", currentUser.backdropImage)
            startActivity(intent)
        }

        // Qui lanciamo una coroutine per ottenere le liste dell'utente corrente
        viewLifecycleOwner.lifecycleScope.launch {
            loadProfileListsAndTimes()
        }

        // Qui lanciamo una coroutine per ottenere le informazioni dell'utente corrente
        viewLifecycleOwner.lifecycleScope.launch {
            loadCurrentUserDetails()
        }

        reloadButton.setOnClickListener {
            // Se l'utente clicca sul bottone di ricarica allora ricarichiamo la pagina
            viewFlipper.displayedChild = 0
            viewLifecycleOwner.lifecycleScope.launch {
                profileViewModel.getLists()
                loadProfileListsAndTimes()
            }
            viewLifecycleOwner.lifecycleScope.launch {
                profileViewModel.loadCurrentUser()
                loadCurrentUserDetails()
            }
        }

        finishedSeriesViewAllButton.setOnClickListener {
            Log.d("ViewAll", "Finished series: $finishedSeries")
            val intent = Intent(requireContext(), ViewAllActivity::class.java)
            intent.putExtra("entities", ArrayList(finishedSeries))
            intent.putExtra("title", "Serie TV completate")
            startActivity(intent)
        }

        return binding.root
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

    private suspend fun loadProfileListsAndTimes() {
        // Collezioniamo le liste dell'utente corrente contenute nella variabile lists del view model
        profileViewModel.lists.collectLatest { lists ->
            if (lists != null) {
                userLists = lists
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
                    } else if (listTitle == "finished_t") {
                        // Se la lista è quella delle serie viste allora aggiorniamo le variabili
                        // tvMinutes e tvNumber con i valori corretti
                        val watchingSeries = FirestoreService.getWatchingSeries(currentUser.uid).first()
                        for (series in watchingSeries) {
                            val seriesDetails = seriesRepository.getSeriesDetails(series.key.toLong())
                            for (season in series.value) {
                                tvMinutes += if (seriesDetails.seasons.any { it.number == 0L }) season.value.sumOf { episode -> seriesDetails.seasons[season.key.toInt()].episodes[episode.toInt() - 1].duration }
                                else season.value.sumOf { episode -> seriesDetails.seasons[season.key.toInt() - 1].episodes[episode.toInt() - 1].duration }
                                tvNumber += season.value.size
                            }
                        }
                        finishedSeries = ids.map { seriesRepository.getSeriesDetails(it) }
                        val firstThreeFinishedSeries = finishedSeries.take(3)
                        val adapter = ViewAllAdapter()
                        adapter.submitList(firstThreeFinishedSeries)
                        finishedSeriesRecyclerView.adapter = adapter
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
                profileListsAdapter = ProfileHorizontalListAdapter(userLists, requireContext())
                binding.horizontalCardsLists.adapter = profileListsAdapter
                profileListsAdapter.submitList(profileListItems)
                viewFlipper.displayedChild = 1
                if (finishedSeriesRecyclerView.adapter?.itemCount == 0) {
                    finishedSeriesViewFlipper.displayedChild = 1
                } else finishedSeriesViewFlipper.displayedChild = 0
            }
        }
    }

    private suspend fun loadCurrentUserDetails() {
        profileViewModel.currentUser.collectLatest { user ->
            if (user != null) {
                // Se l'utente corrente non è nullo allora possiamo procedere con mostrare
                // le informazioni dell'utente corrente
                // Nello specifico mostriamo: il nome visualizzato, lo username e la foto profilo
                currentUser = user
                binding.displayNameText.text = user.nameShown
                binding.usernameText.text = user.username
                Glide.with(this@ProfileFragment).load(user.profileImage)
                    .into(profileImage)
                Glide.with(this@ProfileFragment).load(user.backdropImage)
                    .into(backdropImage)
            }
        }
    }
}