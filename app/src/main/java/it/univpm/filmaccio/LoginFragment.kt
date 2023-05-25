package it.univpm.filmaccio

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.constraintlayout.widget.Constraints.TAG
import androidx.navigation.Navigation
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginFragment : Fragment() {

    companion object {
        private const val RC_GOOGLE_SIGN_IN = 123
    }

    private lateinit var regEmailButton: Button
    private lateinit var buttonEntra: Button
    private lateinit var inputEmailLayout: TextInputLayout
    private lateinit var inputEmail: TextInputEditText
    private lateinit var inputPasswordLayout: TextInputLayout
    private lateinit var inputPassword: TextInputEditText
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var buttonLoginGoogle: Button
    private lateinit var buttonRegGoogle: Button
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        regEmailButton = view.findViewById<Button>(R.id.buttonRegEmail)
        buttonEntra = view.findViewById<Button>(R.id.buttonEntra)
        inputEmailLayout = view.findViewById<TextInputLayout>(R.id.inputLoginUserEmail)
        inputEmail = view.findViewById<TextInputEditText>(R.id.inputUserEmailEditText)
        inputPasswordLayout = view.findViewById<TextInputLayout>(R.id.inputLoginPassword)
        inputPassword = view.findViewById<TextInputEditText>(R.id.inputPasswordEditText)
        buttonLoginGoogle = view.findViewById<Button>(R.id.buttonLoginGoogle)
        buttonRegGoogle = view.findViewById<Button>(R.id.buttonRegGoogle)

        regEmailButton.setOnClickListener { Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_regPrimaFragment)}

        firebaseAuth = FirebaseAuth.getInstance()

        buttonEntra.setOnClickListener {
            inputEmailLayout.isErrorEnabled = false
            inputPasswordLayout.isErrorEnabled = false
            val email = inputEmail.text.toString()
            val password = inputPassword.text.toString()

            loginUserWithEmailAndPassword(email, password)
        }

        buttonLoginGoogle.setOnClickListener {
            signInWithGoogle()
        }
        return view
    }

    private fun loginUserWithEmailAndPassword(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    // Login avvenuto con successo, esegui l'azione desiderata
                    Toast.makeText(requireContext(), "Login avvenuto con successo", Toast.LENGTH_SHORT).show()
                } else {
                    // Login fallito, mostra un messaggio di errore all'utente
                    Toast.makeText(requireContext(), "Login fallito", Toast.LENGTH_SHORT).show()
                    inputEmailLayout.isErrorEnabled = true
                    inputEmailLayout.error = "Indirizzo email o password errati"
                    inputPasswordLayout.isErrorEnabled = true
                    inputPasswordLayout.error = " "
                }
            }
    }

    private fun signInWithGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Login con Google riuscito
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                // Login con Google fallito
                Log.e(TAG, "Google sign-in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Login con Google avvenuto con successo
                    // TODO: far sì che quando l'utente è nuovo, poi si passi alla parte di registrazione per aggiungere i nuovi dati al database
                    val user = firebaseAuth.currentUser
                    val isNewUser = task.result?.additionalUserInfo?.isNewUser ?: false
                    if (isNewUser) {
                        showRegisterMessage()
                    } else {
                        // Esegui le azioni desiderate dopo il login
                    }
                } else {
                    // Login con Google fallito
                    val exception = task.exception
                    // Gestisci l'errore e mostra un messaggio all'utente
                }
            }
    }

    private fun showRegisterMessage() {
        val message = "Non sei ancora registrato, effettua la registrazione per continuare"
        val snackbar = Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG)
        // TODO: Capire come applicare lo stile di Material 3 alla snackbar
        snackbar.setAction("Registrati") {
            buttonRegGoogle.performClick()
        }
        snackbar.show()
    }
}