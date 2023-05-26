package it.univpm.filmaccio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class PasswordResetFragment : Fragment() {

    private lateinit var emailPasswordResetTextEdit: EditText
    private lateinit var buttonInviaReset: Button
    private lateinit var buttonBackReset : Button
    private lateinit var db: FirebaseFirestore
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_password_reset, container, false)

        db = FirebaseFirestore.getInstance()

        emailPasswordResetTextEdit = view.findViewById(R.id.emailPasswordResetTextEdit)
        buttonInviaReset = view.findViewById(R.id.buttonInviaReset)
        buttonBackReset = view.findViewById(R.id.buttonBackReset)

        buttonInviaReset.setOnClickListener {
            val email = emailPasswordResetTextEdit.text.toString()
            db.collection("users").whereEqualTo("email", email).get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        emailPasswordResetTextEdit.text = null
                    } else {
                        emailPasswordResetTextEdit.text = null
                        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    }
                }
            val snackbar = Snackbar.make(
                view,
                "Se l'email è corretta, riceverai una mail per il reset della password",
                Snackbar.LENGTH_LONG
            )
            snackbar.setAction("Torna al login") {
                Navigation.findNavController(view)
                    .navigate(R.id.action_passwordResetFragment_to_loginFragment)
            }
            snackbar.show()
        }

        buttonBackReset.setOnClickListener {
            Navigation.findNavController(view)
                .navigate(R.id.action_passwordResetFragment_to_loginFragment)
        }
        return view
    }
}