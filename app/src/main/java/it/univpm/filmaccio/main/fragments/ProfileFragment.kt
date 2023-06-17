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

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val profileViewModel: ProfileViewModel by viewModels()

    private val movieRepository = MovieRepository()
    private val seriesRepository = SeriesRepository()

    private lateinit var profileListsAdapter: ProfileHorizontalListAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding
            .inflate(inflater, container, false)

        profileListsAdapter = ProfileHorizontalListAdapter()

        viewLifecycleOwner.lifecycleScope.launch {
            profileViewModel.lists.collect { lists ->
                if (lists != null) {
                    val profileListItems = lists.flatMap { entry ->
                        val listTitle = entry.key
                        val ids = entry.value
                        if (ids.size >= 3) {
                            val id1 = ids[0]
                            val id2 = ids[1]
                            val id3 = ids[2]
                            if (listTitle.last() == 'm') listOf(movieRepository.convertIdToProfileListItem(id1, id2, id3, listTitle))
                            else listOf(seriesRepository.convertIdToProfileListItem(id1, id2, id3, listTitle))
                        } else {
                            val id1 = ids.getOrNull(0) ?: 0L
                            val id2 = ids.getOrNull(1) ?: 0L
                            val id3 = ids.getOrNull(2) ?: 0L
                            if (listTitle.last() == 'm') listOf(movieRepository.convertIdToProfileListItem(id1, id2, id3, listTitle))
                            else listOf(seriesRepository.convertIdToProfileListItem(id1, id2, id3, listTitle))
                        }
                    }

                    profileListsAdapter.submitList(profileListItems)
                }
            }
        }

        binding.listeHorizontalList.adapter = profileListsAdapter


        viewLifecycleOwner.lifecycleScope.launch {
            profileViewModel.currentUser.collect { user ->
                if (user != null) {
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