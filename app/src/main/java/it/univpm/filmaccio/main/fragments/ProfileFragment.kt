package it.univpm.filmaccio.main.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import it.univpm.filmaccio.data.repository.MovieRepository
import it.univpm.filmaccio.data.repository.SeriesRepository
import it.univpm.filmaccio.databinding.FragmentProfileBinding
import it.univpm.filmaccio.main.adapters.ProfileHorizontalListAdapter
import it.univpm.filmaccio.main.viewmodels.ProfileViewModel
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

    private val profileViewModel: ProfileViewModel by viewModels()

    // Qui ci riferiamo ai repository per poter convertire le liste in ProfileListItem
    private val movieRepository = MovieRepository()
    private val seriesRepository = SeriesRepository()

    // Anche qui abbiamo un adapter per la recycler view delle liste
    private lateinit var profileListsAdapter: ProfileHorizontalListAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding
            .inflate(inflater, container, false)

        profileListsAdapter = ProfileHorizontalListAdapter()

        // Qui lanciamo una coroutine per ottenere le liste dell'utente corrente
        viewLifecycleOwner.lifecycleScope.launch {
            // Collezioniamo le liste dell'utente corrente contenute nella variabile lists del view model
            profileViewModel.lists.collect { lists ->
                if (lists != null) {
                    // Se la variabile lists non è vuota allora possiamo procedere con il creare
                    // la lista di ProfileListItem da passare all'adapter
                    val profileListItems = lists.flatMap { entry ->
                        // considerando che ogni lista è una coppia (titolo, lista di id), qui
                        // otteniamo il titolo e la lista di id in variabili separate
                        val listTitle = entry.key
                        val ids = entry.value
                        if (ids.size >= 3) {
                            // Se la lista di id è lunga almeno 3 allora possiamo creare una lista
                            // di ProfileListItem con i primi 3 id della lista di id e il titolo
                            val id1 = ids[0]
                            val id2 = ids[1]
                            val id3 = ids[2]
                            // Qui controlliamo se il titolo della lista finisce con 'm' o 't' per
                            // capire se la lista è di film o di serie TV e chiamare il metodo giusto
                            if (listTitle.last() == 'm') listOf(movieRepository.convertIdToProfileListItem(id1, id2, id3, listTitle))
                            else listOf(seriesRepository.convertIdToProfileListItem(id1, id2, id3, listTitle))
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
                            if (listTitle.last() == 'm') listOf(movieRepository.convertIdToProfileListItem(id1, id2, id3, listTitle))
                            else listOf(seriesRepository.convertIdToProfileListItem(id1, id2, id3, listTitle))
                        }
                    }

                    // una volta creata la lista di ProfileListItem la passiamo all'adapter in modo che
                    // lui possa fare il resto e mostrare le liste nella recycler view
                    profileListsAdapter.submitList(profileListItems)
                }
            }
        }

        binding.listeHorizontalList.adapter = profileListsAdapter


        // Qui lanciamo una coroutine per ottenere le informazioni dell'utente corrente
        viewLifecycleOwner.lifecycleScope.launch {
            profileViewModel.currentUser.collect { user ->
                if (user != null) {
                    // Se l'utente corrente non è nullo allora possiamo procedere con mostrare
                    // le informazioni dell'utente corrente
                    // Nello specifico mostriamo: il nome visualizzato, lo username e la foto profilo
                    binding.displayNameText.text = user.nameShown
                    binding.usernameText.text = user.username
                    Glide.with(this@ProfileFragment)
                        .load(user.profileImage)
                        .into(binding.profileImage)
                }
            }
        }

        return binding.root
    }
}