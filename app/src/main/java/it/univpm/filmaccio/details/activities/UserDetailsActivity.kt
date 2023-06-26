package it.univpm.filmaccio.details.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import it.univpm.filmaccio.R
import it.univpm.filmaccio.main.utils.FirestoreService.followUser
import it.univpm.filmaccio.details.viewmodels.UserDetailsViewModel
import it.univpm.filmaccio.main.utils.FirestoreService.collectionFollow
import it.univpm.filmaccio.main.utils.UserUtils
import it.univpm.filmaccio.main.utils.FirestoreService.getFollowers
import kotlinx.coroutines.flow.toList
import it.univpm.filmaccio.main.utils.UserUtils.auth
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import org.checkerframework.common.subtyping.qual.Bottom

class UserDetailsActivity : AppCompatActivity() {

    private lateinit var userDetailsViewModel: UserDetailsViewModel
    private lateinit var profileImage: ShapeableImageView
    private lateinit var displayNameTextView: TextView
    private lateinit var usernameTextView: TextView
    private lateinit var backdropImageView: ShapeableImageView
    private lateinit var seguiBotton : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_details)
        val nameShown = intent.getStringExtra("nameShown")
        val username = intent.getStringExtra("username")
        val propic = intent.getStringExtra("propic")
        val backdropImage = intent.getStringExtra("backdropImage")
        seguiBotton=findViewById(R.id.button_follow)
        displayNameTextView=findViewById(R.id.displayNameText)
        usernameTextView=findViewById(R.id.usernameText)
        profileImage=findViewById(R.id.profileImage)
        //backdropImageView=findViewById(R.id.backdropImage)


        displayNameTextView.setText(nameShown)
        usernameTextView.setText(username)
        Glide.with(this).load(propic).into(profileImage)
        val auth = UserUtils.auth
        val currentUserUid = auth.uid
        //Glide.with(this).load(backdropImage).into(backdropImageView)

        fun getFollowers(uid: String) = flow {
            val doc = collectionFollow.document(uid).get().await()
            if (doc.exists()) {
                val followers = doc.get("followers") as? List<String> ?: emptyList()
                emit(followers)
            } else {
                // Gestisci il caso in cui il documento non esiste.
                // Qui, emettiamo una lista vuota.
                emit(emptyList<String>())
            }
        }


        val targetUid = intent.getStringExtra("uid")
        seguiBotton.setOnClickListener {
            followUser(currentUserUid!!, targetUid!!)

        }

    }
}