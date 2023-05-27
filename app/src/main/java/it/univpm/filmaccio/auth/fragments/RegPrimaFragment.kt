package it.univpm.filmaccio.auth.fragments

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.Navigation
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import it.univpm.filmaccio.R
import it.univpm.filmaccio.RegPrimaFragmentDirections

class RegPrimaFragment : Fragment() {

    private lateinit var buttonBack: Button
    private lateinit var buttonAvanti: Button
    private lateinit var emailRegInputTextLayout: TextInputLayout
    private lateinit var usernameRegInputTextLayout: TextInputLayout
    private lateinit var passwordRegInputTextLayout: TextInputLayout
    private lateinit var passwordConfirmRegInputTextLayout: TextInputLayout
    private lateinit var emailRegInputTextEdit: TextInputEditText
    private lateinit var usernameRegInputTextEdit: TextInputEditText
    private lateinit var passwordRegInputTextEdit: TextInputEditText
    private lateinit var passwordConfirmRegInputTextEdit: TextInputEditText
    private val db = Firebase.firestore
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_reg_prima, container, false)

        buttonBack = view.findViewById(R.id.buttonBack)
        buttonAvanti = view.findViewById(R.id.buttonFine)
        emailRegInputTextLayout = view.findViewById(R.id.emailRegInputLayout)
        usernameRegInputTextLayout = view.findViewById(R.id.usernameRegInputLayout)
        passwordRegInputTextLayout = view.findViewById(R.id.passwordRegInputLayout)
        passwordConfirmRegInputTextLayout = view.findViewById(R.id.passwordConfirmRegInputLayout)
        emailRegInputTextEdit = view.findViewById(R.id.emailRegInputTextEdit)
        usernameRegInputTextEdit = view.findViewById(R.id.usernameRegInputTextEdit)
        passwordRegInputTextEdit = view.findViewById(R.id.passwordRegInputTextEdit)
        passwordConfirmRegInputTextEdit = view.findViewById(R.id.passwordConfirmRegInputTextEdit)

        buttonBack.setOnClickListener {
            Navigation.findNavController(view)
                .navigate(R.id.action_regPrimaFragment_to_loginFragment)
        }
        buttonAvanti.setOnClickListener {
            val email = emailRegInputTextEdit.text.toString()
            val username = usernameRegInputTextEdit.text.toString().lowercase()
            val password = passwordRegInputTextEdit.text.toString()
            val passConfirm = passwordConfirmRegInputTextEdit.text.toString()
            emailRegInputTextLayout.isErrorEnabled = false
            usernameRegInputTextLayout.isErrorEnabled = false
            passwordRegInputTextLayout.isErrorEnabled = false
            passwordConfirmRegInputTextLayout.isErrorEnabled = false

            if (!isEmailValid(email)) {
                emailRegInputTextLayout.isErrorEnabled = true
                emailRegInputTextLayout.error = "Indirizzo email non valido"
                return@setOnClickListener
            }

            if (!isUsernameValid(username)) {
                usernameRegInputTextLayout.isErrorEnabled = true
                usernameRegInputTextLayout.error =
                    "Il nome utente deve essere lungo almeno 3 caratteri, deve contenere almeno una lettera e può contenere solo lettere, numeri e i caratteri '.' e '_'"
                return@setOnClickListener
            }

            if (!isPasswordValid(password)) {
                passwordRegInputTextLayout.isErrorEnabled = true
                passwordRegInputTextLayout.error =
                    "La password deve essere lunga almeno 8 caratteri e contenere almeno una lettera maiuscola, una minuscola e un numero"
                return@setOnClickListener
            }

            if (password != passConfirm) {
                passwordConfirmRegInputTextLayout.isErrorEnabled = true
                passwordConfirmRegInputTextLayout.error = "Le password non corrispondono"
                return@setOnClickListener
            }

            isEmailAlreadyRegistered(email) { emailExists ->
                if (emailExists) {
                    emailRegInputTextLayout.isErrorEnabled = true
                    emailRegInputTextLayout.error =
                        "L'indirizzo email è già associato ad un account esistente"
                } else {
                    isUsernameAlreadyRegistered(username) { usernameExists ->
                        if (usernameExists) {
                            usernameRegInputTextLayout.isErrorEnabled = true
                            usernameRegInputTextLayout.error = "Il nome utente non è disponibile"
                        } else {
                            val action =
                                RegPrimaFragmentDirections.actionRegPrimaFragmentToRegSecondaFragment(
                                    email,
                                    username,
                                    password
                                )
                            Navigation.findNavController(view).navigate(action)
                        }
                    }
                }
            }
            if (usernameRegInputTextLayout.isErrorEnabled || emailRegInputTextLayout.isErrorEnabled) {
                return@setOnClickListener
            }
        }
        return view
    }

    private fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isEmailAlreadyRegistered(email: String, callback: (Boolean) -> Unit) {
        val query = db.collection("users").whereEqualTo("email", email)
        query.get().addOnSuccessListener { querySnapshot ->
            val exists = !querySnapshot.isEmpty
            callback(exists)
        }
            .addOnFailureListener {
                callback(false)
            }
    }

    private fun isUsernameValid(username: String): Boolean {
        // Verifica che il nome utente non contenga caratteri speciali
        val pattern = "^(?=.*[a-z])[a-z0-9_.]{3,30}$".toRegex()
        return pattern.matches(username)
    }

    private fun isUsernameAlreadyRegistered(username: String, callback: (Boolean) -> Unit) {
        db.collection("users").whereEqualTo("username", username).get()
            .addOnSuccessListener { querySnapshot ->
                val exists = !querySnapshot.isEmpty
                callback(exists)
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    private fun isPasswordValid(password: String): Boolean {
        val pattern = Regex("^(?=.*[a-z])(?=.*\\d).{8,}$")
        return password.matches(pattern)
    }
}