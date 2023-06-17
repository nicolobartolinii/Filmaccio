package it.univpm.filmaccio.main.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import it.univpm.filmaccio.databinding.FragmentProfileBinding
import it.univpm.filmaccio.main.adapters.ProfileHorizontalListAdapter
import it.univpm.filmaccio.main.viewmodels.ProfileViewModel
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val profileViewModel: ProfileViewModel by viewModels()

    private lateinit var profileListsAdapter: ProfileHorizontalListAdapter
    private lateinit var lists: Map<String, Any>
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding
            .inflate(inflater, container, false)

        lists = profileViewModel.lists.value ?: emptyMap()
        for (list in lists) {
            val listName = list.key
            val content = list.value as List<*>
            val posters = listOf("", "", "")
            if (listName.last() == 'm') {
                for (movie in content) {
                    val poster = movie
                    posters[i] = poster
                }
            }
        }
        profileListsAdapter = ProfileHorizontalListAdapter()

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