package it.univpm.filmaccio.auth.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Timestamp
import it.univpm.filmaccio.R
import it.univpm.filmaccio.databinding.FragmentRegGooglePrimoBinding
import it.univpm.filmaccio.main.utils.FirestoreService
import java.util.Calendar

/**
 * Questo fragment è il primo passo della registrazione tramite Google, in cui l'utente inserisce
 * il proprio username (che deve essere univoco), il proprio genere e la propria data di nascita
 *
 * Questo passaggio è praticamente identico al secondo passo della registrazione tramite email,
 * quindi per i commenti si rimanda a RegSecondoFragment.
 *
 * @see RegSecondaFragment
 *
 * @author nicolobartolinii
 */
class RegGooglePrimoFragment : Fragment() {

    private var _binding: FragmentRegGooglePrimoBinding? = null
    private val binding get() = _binding!!

    private lateinit var buttonBack: Button
    private lateinit var buttonAvanti: Button
    private lateinit var usernameRegInputTextLayout: TextInputLayout
    private lateinit var usernameRegInputTextEdit: TextInputEditText
    private lateinit var genereRadioGroup: RadioGroup
    private lateinit var dataNascitaDatePicker: DatePicker
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegGooglePrimoBinding.inflate(inflater, container, false)

        buttonBack = binding.buttonBack
        buttonAvanti = binding.buttonAvanti
        usernameRegInputTextLayout = binding.usernameRegInputLayout
        usernameRegInputTextEdit = binding.usernameRegInputTextEdit
        genereRadioGroup = binding.genereRadioGroup
        dataNascitaDatePicker = binding.spinnerData

        buttonBack.setOnClickListener {
            Navigation.findNavController(binding.root)
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

            if (gender == null) {
                Toast.makeText(requireContext(), "Seleziona un genere", Toast.LENGTH_SHORT).show()
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
                    "Inserisci una data di nascita valida (almeno $minimumAge anni)",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, day)

            val birthDate = Timestamp(calendar.time)

            val action =
                RegGooglePrimoFragmentDirections.actionRegGooglePrimoFragmentToRegGoogleSecondoFragment(
                    username, gender, birthDate
                )
            isUsernameAlreadyRegistered(username) { usernameExists ->
                if (usernameExists) {
                    usernameRegInputTextLayout.isErrorEnabled = true
                    usernameRegInputTextLayout.error = "Il nome utente non è disponibile"
                } else {
                    Navigation.findNavController(binding.root).navigate(action)
                }
            }
        }
        return binding.root
    }

    private fun isUsernameValid(username: String): Boolean {
        val pattern = "^(?=.*[a-z])[a-z0-9_.]{3,30}$".toRegex()
        return pattern.matches(username)
    }

    private fun isUsernameAlreadyRegistered(username: String, callback: (Boolean) -> Unit) {
        FirestoreService.getWhereEqualTo("users", "username", username).get()
            .addOnSuccessListener { querySnapshot ->
                val exists = !querySnapshot.isEmpty
                callback(exists)
            }.addOnFailureListener {
                callback(true)
            }
    }

}