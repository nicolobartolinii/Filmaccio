package it.univpm.filmaccio

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.navigation.Navigation
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

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
    private lateinit var buttonEntraGoogle: Button
    private lateinit var buttonForgotPassword: Button
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        regEmailButton = view.findViewById(R.id.buttonRegEmail)
        buttonEntra = view.findViewById(R.id.buttonEntra)
        inputEmailLayout = view.findViewById(R.id.inputLoginUserEmail)
        inputEmail = view.findViewById(R.id.inputUserEmailEditText)
        inputPasswordLayout = view.findViewById(R.id.inputLoginPassword)
        inputPassword = view.findViewById(R.id.inputPasswordEditText)
        buttonEntraGoogle = view.findViewById(R.id.buttonEntraGoogle)
        buttonForgotPassword = view.findViewById(R.id.buttonForgotPassword)

        regEmailButton.setOnClickListener {
            Navigation.findNavController(view)
                .navigate(R.id.action_loginFragment_to_regPrimaFragment)
        }

        firebaseAuth = FirebaseAuth.getInstance()

        buttonEntra.setOnClickListener {
            inputEmailLayout.isErrorEnabled = false
            inputPasswordLayout.isErrorEnabled = false
            val email = inputEmail.text.toString()
            val password = inputPassword.text.toString()

            loginUserWithEmailAndPassword(email, password)
        }

        buttonEntraGoogle.setOnClickListener {
            signInWithGoogle()
        }

        buttonForgotPassword.setOnClickListener {
            Navigation.findNavController(view)
                .navigate(R.id.action_loginFragment_to_passwordResetFragment)
        }
        return view
    }

    private fun loginUserWithEmailAndPassword(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val intent = Intent(requireContext(), HomeActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                } else {
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
                // Ottenimento dell'account Google
                val account = task.getResult(ApiException::class.java)

                // Autenticazione con Google tramite Firebase Authentication
                val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
                FirebaseAuth.getInstance().signInWithCredential(credential)
                    .addOnCompleteListener { authTask ->
                        if (authTask.isSuccessful) {
                            val user = FirebaseAuth.getInstance().currentUser

                            // Verifica se l'utente esiste già nel database
                            val userRef = FirebaseFirestore.getInstance().collection("users").whereEqualTo("uid", user!!.uid)

                            userRef.get()
                                .addOnCompleteListener { userTask ->
                                    if (userTask.isSuccessful && !userTask.result.isEmpty) {
                                        // L'utente esiste nel database, passa alla HomeActivity
                                        startHomeActivity()
                                    } else {
                                        // L'utente non esiste nel database, passa alla registrazione tramite Google
                                        Navigation.findNavController(requireView())
                                            .navigate(R.id.action_loginFragment_to_regGooglePrimoFragment)
                                    }
                                }
                        } else {
                            // Login con Google fallito
                            Toast.makeText(requireContext(), "Errore durante l'accesso con Google (Errore: ${task.exception.toString()})", Toast.LENGTH_SHORT).show()
                        }
                    }

            } catch (e: ApiException) {
                // Si è verificato un errore durante l'accesso con Google
                Toast.makeText(requireContext(), "Errore durante l'accesso con Google (Errore: ${task.exception.toString()})", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startHomeActivity() {
        val intent = Intent(requireContext(), HomeActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }
}