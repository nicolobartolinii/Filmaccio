package it.univpm.filmaccio

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.navigation.Navigation
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {

    private lateinit var regEmailButton: Button
    private lateinit var buttonEntra: Button
    private lateinit var inputEmailLayout: TextInputLayout
    private lateinit var inputEmail: TextInputEditText
    private lateinit var inputPasswordLayout: TextInputLayout
    private lateinit var inputPassword: TextInputEditText
    private lateinit var firebaseAuth: FirebaseAuth
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

        regEmailButton.setOnClickListener { Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_regPrimaFragment)}

        firebaseAuth = FirebaseAuth.getInstance()

        buttonEntra.setOnClickListener {
            inputEmailLayout.isErrorEnabled = false
            inputPasswordLayout.isErrorEnabled = false
            val email = inputEmail.text.toString()
            val password = inputPassword.text.toString()

            loginUserWithEmailAndPassword(email, password)
        }
        return view
    }

    private fun loginUserWithEmailAndPassword(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
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
}