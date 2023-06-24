package it.univpm.filmaccio.main.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import it.univpm.filmaccio.data.models.User
import it.univpm.filmaccio.data.repository.MovieRepository
import it.univpm.filmaccio.data.repository.SeriesRepository
import it.univpm.filmaccio.databinding.FragmentProfileBinding
import it.univpm.filmaccio.main.EditProfileActivity
import it.univpm.filmaccio.main.SettingsActivity
import it.univpm.filmaccio.main.adapters.ProfileHorizontalListAdapter
import it.univpm.filmaccio.main.viewmodels.ProfileViewModel
import kotlinx.coroutines.flow.collectLatest
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

    private lateinit var editProfileButton: Button // bottone per la pagina di modifica
    private lateinit var settingsButton: Button // bottone per le impostazioni
    private lateinit var currentUser: User
    private var movieMinutes = 0
    private var tvMinutes = 0
    private var movieNumber = 0
    private var tvNumber = 0
    private var followersNumber = 0
    private var followingNumber = 0

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

        profileListsAdapter = ProfileHorizontalListAdapter()

        // dichiarazione bottoni
        settingsButton = binding.settingsButton
        editProfileButton = binding.modifyProfileButton

        val viewFlipper = binding.viewFlipper

        viewFlipper.displayedChild = 0

        settingsButton.setOnClickListener {
            val intent = Intent(requireContext(), SettingsActivity::class.java)
            startActivity(intent)
        }

        editProfileButton.setOnClickListener {
            val intent = Intent(requireContext(), EditProfileActivity::class.java)
            intent.putExtra("uid", currentUser.uid)
            intent.putExtra("user", currentUser.nameShown)
            intent.putExtra("email", currentUser.email)
            startActivity(intent)

        }

        // Qui lanciamo una coroutine per ottenere le liste dell'utente corrente
        viewLifecycleOwner.lifecycleScope.launch {
            // Collezioniamo le liste dell'utente corrente contenute nella variabile lists del view model
            profileViewModel.lists.collectLatest { lists ->
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
                    profileListsAdapter.submitList(profileListItems)
                    viewFlipper.displayedChild = 1
                }
            }
        }

        binding.listeHorizontalList.adapter = profileListsAdapter

        // Qui lanciamo una coroutine per ottenere le informazioni dell'utente corrente
        viewLifecycleOwner.lifecycleScope.launch {
            profileViewModel.currentUser.collectLatest { user ->
                if (user != null) {
                    // Se l'utente corrente non è nullo allora possiamo procedere con mostrare
                    // le informazioni dell'utente corrente
                    // Nello specifico mostriamo: il nome visualizzato, lo username e la foto profilo
                    currentUser = user
                    binding.displayNameText.text = user.nameShown
                    binding.usernameText.text = user.username
                    Glide.with(this@ProfileFragment).load(user.profileImage)
                        .into(binding.profileImage)
                }
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.viewFlipper.displayedChild =
            0 // Imposta la schermata di caricamento come schermata iniziale
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