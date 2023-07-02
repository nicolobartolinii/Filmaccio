package it.univpm.filmaccio.main.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import android.widget.ViewFlipper
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import it.univpm.filmaccio.databinding.FragmentFeedBinding
import it.univpm.filmaccio.main.adapters.FeedAdapter
import it.univpm.filmaccio.main.adapters.PeopleAdapter
import it.univpm.filmaccio.main.viewmodels.FeedViewModel
import kotlinx.coroutines.launch

// Questo fragment è la schermata in cui dovrebbe essere mostrato il feed di film, serie TV, persone
// e altri utenti all'utente. Per ora non è stato implementato nulla.
class FeedFragment : Fragment() {
    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!

    private val feedViewModel: FeedViewModel by viewModels()

    private lateinit var reloadButton: Button
    private lateinit var infoButton: Button
    private lateinit var feedAdapter: FeedAdapter
    private lateinit var feedViewFlipper: ViewFlipper
    private lateinit var peopleAdapter: PeopleAdapter
    private lateinit var peopleViewFlipper: ViewFlipper

    private lateinit var feedRecyclerView: RecyclerView
    private lateinit var peopleRecyclerView: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeedBinding
            .inflate(inflater, container, false)

        reloadButton = binding.buttonReload
        infoButton = binding.buttonInfo
        feedRecyclerView = binding.recyclerViewFollowedUsersFeed
        feedViewFlipper = binding.feedViewFlipper
        peopleRecyclerView = binding.recyclerViewFollowedPeopleFeed
        peopleViewFlipper = binding.peopleViewFlipper

        feedViewFlipper.displayedChild = 0
        peopleViewFlipper.displayedChild = 0

        peopleRecyclerView.layoutManager = GridLayoutManager(requireContext(), 3)

        // feedAdapter = FeedAdapter()

        feedViewModel.usersFeed.observe(viewLifecycleOwner) { feed ->
            if (feed == null) feedViewFlipper.displayedChild = 0
            else if (feed.isEmpty()) feedViewFlipper.displayedChild = 2
            else {
                feedAdapter = FeedAdapter(feed, requireContext())
                feedRecyclerView.adapter = feedAdapter
                feedViewFlipper.displayedChild = 1
            }
        }

        feedViewModel.followedPeople.observe(viewLifecycleOwner) { people ->
            if (people == null) peopleViewFlipper.displayedChild = 0
            else if (people.isEmpty()) peopleViewFlipper.displayedChild = 2
            else {
                peopleAdapter = PeopleAdapter(people, requireContext())
                peopleRecyclerView.adapter = peopleAdapter
                peopleViewFlipper.displayedChild = 1
            }
        }

        reloadButton.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                feedViewModel.loadFollowedPeople()
            }
            viewLifecycleOwner.lifecycleScope.launch {
                feedViewModel.loadUsersFeed()
            }
            feedViewFlipper.displayedChild = 0
            peopleViewFlipper.displayedChild = 0
        }

        infoButton.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext()).setTitle("Info")
                .setMessage("In questa schermata puoi osservare l'ultima recensione effettuata dagli utenti che segui e la lista dei personaggi che segui.\n\n" +
                        "Per aggiungere un utente alla lista dei seguiti, vai nella schermata del suo profilo e clicca sul pulsante \"SEGUI\". Se non trovi un utente che segui in questa lista significa che non ha mai recensito nessun prodotto, potrai trovarlo comunque nella scheda degli utenti seguiti nel tuo profilo.\n\n" +
                        "Per aggiungere un personaggio alla lista dei seguiti, cercalo nella schermata di ricerca o nei dettagli di un prodotto in cui è presente e clicca sul pulsante \"SEGUI\".\n\n" +
                        "A volte, quando aggiungi un utente o un personaggio alla lista dei seguiti, potrebbe non comparire subito in questa schermata. In questo caso, clicca sul pulsante accanto a quello di informazioni per aggiornare le liste.")
                .setPositiveButton("Ok") { dialog, _ ->
                    dialog.dismiss()
                }.show()
        }

        return binding.root
    }
}