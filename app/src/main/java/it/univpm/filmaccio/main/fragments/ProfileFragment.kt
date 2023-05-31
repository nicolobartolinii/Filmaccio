package it.univpm.filmaccio.main.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import it.univpm.filmaccio.R
import it.univpm.filmaccio.databinding.FragmentFeedBinding
import it.univpm.filmaccio.databinding.FragmentProfileBinding
import it.univpm.filmaccio.main.viewmodels.FeedViewModel
import it.univpm.filmaccio.main.viewmodels.ProfileViewModel
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val profileViewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding
            .inflate(inflater, container, false)

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