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
import it.univpm.filmaccio.main.utils.FirestoreService.followUser
import it.univpm.filmaccio.details.viewmodels.UserDetailsViewModel
import it.univpm.filmaccio.details.viewmodels.UserDetailsViewModelFactory
import it.univpm.filmaccio.main.utils.FirestoreService

import it.univpm.filmaccio.main.utils.FirestoreService.getFollowers
import it.univpm.filmaccio.main.utils.FirestoreService.getList

import it.univpm.filmaccio.main.utils.UserUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class UserDetailsActivity : AppCompatActivity() {

    private lateinit var userDetailsViewModel: UserDetailsViewModel
    private lateinit var profileImage: ShapeableImageView
    private lateinit var displayNameTextView: TextView
    private lateinit var usernameTextView: TextView
    private lateinit var backdropImageView: ShapeableImageView
    private lateinit var seguiBotton: Button
    private lateinit var filmVistiTextView: TextView
    private lateinit var followerTextView: TextView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_details)
        val nameShown = intent.getStringExtra("nameShown")
        val username = intent.getStringExtra("username")
        val propic = intent.getStringExtra("profileImage")
        val backdropImage = intent.getStringExtra("backdropImage")
        seguiBotton = findViewById(R.id.button_follow)
        displayNameTextView = findViewById(R.id.displayNameText)
        usernameTextView = findViewById(R.id.usernameText)
        profileImage = findViewById(R.id.profileImage)
        backdropImageView=findViewById(R.id.backdropImage)
        filmVistiTextView=findViewById(R.id.filmvisti)
        followerTextView=findViewById(R.id.followers)



        displayNameTextView.text = nameShown
        usernameTextView.text = username
        Glide.with(this).load(propic).into(profileImage)
        Glide.with(this).load(backdropImage).into(backdropImageView)
        val auth = UserUtils.auth
        val currentUserUid = auth.uid
        val targetUid = intent.getStringExtra("uid")!!


        val followersFlow = getFollowers(targetUid) // chiamata alla funzione che ritorna il numero di follower
        val watchedMoviesFlow = getList(targetUid, "watched_m")

        GlobalScope.launch(Dispatchers.Main) {
            // funzione che mette nel textview il numero di follower
            followersFlow.collect { followers ->
                followerTextView.text = followers.size.toString()
            }
        }
        GlobalScope.launch(Dispatchers.Main) {
            watchedMoviesFlow.collect { watchedMovies ->
                filmVistiTextView.text = watchedMovies.size.toString()
            }
        }



        userDetailsViewModel = ViewModelProvider(
            this,
            UserDetailsViewModelFactory(targetUid)
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
            } else {
                FirestoreService.unfollowUser(currentUserUid!!, targetUid)
                seguiBotton.text = "SEGUI"
            }
        }
    }
}