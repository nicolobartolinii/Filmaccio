package it.univpm.filmaccio

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.RadioGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Calendar

class RegGooglePrimoFragment : Fragment() {

    private lateinit var buttonBack: Button
    private lateinit var buttonAvanti: Button
    private lateinit var usernameRegInputTextLayout: TextInputLayout
    private lateinit var usernameRegInputTextEdit: TextInputEditText
    private lateinit var genereRadioGroup: RadioGroup
    private lateinit var dataNascitaDatePicker: DatePicker
    private val db = Firebase.firestore
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_reg_google_primo, container, false)

        buttonBack = view.findViewById(R.id.buttonBack)
        buttonAvanti = view.findViewById(R.id.buttonAvanti)
        usernameRegInputTextLayout = view.findViewById(R.id.usernameRegInputLayout)
        usernameRegInputTextEdit = view.findViewById(R.id.usernameRegInputTextEdit)
        genereRadioGroup = view.findViewById(R.id.genereRadioGroup)
        dataNascitaDatePicker = view.findViewById(R.id.spinnerData)

        buttonBack.setOnClickListener {
            Navigation.findNavController(view)
                .navigate(R.id.action_regGooglePrimoFragment_to_loginFragment)
        }
        buttonAvanti.setOnClickListener {
            val username = usernameRegInputTextEdit.text.toString().lowercase()
            usernameRegInputTextLayout.isErrorEnabled = false

            val gender = when (genereRadioGroup.checkedRadioButtonId) {
                R.id.maschileRadioButton -> "M"
                R.id.femminileRadioButton -> "F"
                R.id.altroGenereRadioButton -> "A"
                else -> null
            }

            val day = dataNascitaDatePicker.dayOfMonth
            val month = dataNascitaDatePicker.month
            val year = dataNascitaDatePicker.year

            if (!isUsernameValid(username)) {
                usernameRegInputTextLayout.isErrorEnabled = true
                usernameRegInputTextLayout.error =
                    "Il nome utente deve essere lungo almeno 3 caratteri, deve contenere almeno una lettera e può contenere solo lettere, numeri e i caratteri '.' e '_'"
                return@setOnClickListener
            }

            isUsernameAlreadyRegistered(username) { usernameExists ->
                if (usernameExists) {
                    usernameRegInputTextLayout.isErrorEnabled = true
                    usernameRegInputTextLayout.error = "Il nome utente non è disponibile"
                }
            }

            if (usernameRegInputTextLayout.isErrorEnabled) {
                return@setOnClickListener
            }

            if (gender == null) {
                Toast.makeText(requireContext(), "Seleziona un genere", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val calendar = Calendar.getInstance()
            val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
            val currentMonth = calendar.get(Calendar.MONTH)
            val currentYear = calendar.get(Calendar.YEAR)

            val minimumAge = 14

            var age = currentYear - year

            if (currentMonth < month || (currentMonth == month && currentDay < day)) {
                age--
            }

            val isValidAge = age >= minimumAge

            if (!isValidAge) {
                Toast.makeText(
                    requireContext(),
                    "Inserisci una data di nascita valida (almeno 14 anni)",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, day)

            val birthDate = Timestamp(calendar.time)

            val action = RegGooglePrimoFragmentDirections.actionRegGooglePrimoFragmentToRegGoogleSecondoFragment(username, gender, birthDate)
            Navigation.findNavController(view).navigate(action)
        }
        return view
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

}