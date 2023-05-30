package it.univpm.filmaccio.main.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import it.univpm.filmaccio.R
import it.univpm.filmaccio.databinding.FragmentFeedBinding
import it.univpm.filmaccio.databinding.FragmentProfileBinding
import it.univpm.filmaccio.main.viewmodels.FeedViewModel
import it.univpm.filmaccio.main.viewmodels.ProfileViewModel

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

        val currentUser = profileViewModel.currentUser

        Glide.with(this)
            .load(currentUser?.profileImage)
            .into(binding.profileImage)

        binding.displayNameText.text = currentUser?.nameShown
        binding.usernameText.text = currentUser?.username

        return binding.root
    }
}