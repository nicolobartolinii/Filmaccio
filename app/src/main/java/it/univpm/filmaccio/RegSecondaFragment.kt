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
import androidx.navigation.fragment.navArgs
import com.google.firebase.Timestamp
import java.util.Calendar

class RegSecondaFragment : Fragment() {

    private lateinit var buttonBack: Button
    private lateinit var buttonAvanti: Button
    private lateinit var genereRadioGroup: RadioGroup
    private lateinit var dataNascitaDatePicker: DatePicker
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_reg_seconda, container, false)

        buttonBack = view.findViewById<Button>(R.id.buttonBack)
        buttonAvanti = view.findViewById<Button>(R.id.buttonFine)
        genereRadioGroup = view.findViewById(R.id.genereRadioGroup)
        dataNascitaDatePicker = view.findViewById(R.id.spinnerData)

        buttonBack.setOnClickListener { Navigation.findNavController(view).navigate(R.id.action_regSecondaFragment_to_regPrimaFragment)}
        buttonAvanti.setOnClickListener {
            val gender = when (genereRadioGroup.checkedRadioButtonId) {
                R.id.maschileRadioButton -> "M"
                R.id.femminileRadioButton -> "F"
                R.id.altroGenereRadioButton -> "A"
                else -> null
            }

            val day = dataNascitaDatePicker.dayOfMonth
            val month = dataNascitaDatePicker.month
            val year = dataNascitaDatePicker.year

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
                Toast.makeText(requireContext(), "Inserisci una data di nascita valida (almeno 14 anni)", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, day)

            val birthDate = Timestamp(calendar.time)

            val args: RegSecondaFragmentArgs by navArgs()
            val email: String = args.email
            val username: String = args.username
            val password: String = args.password

            val action = RegSecondaFragmentDirections.actionRegSecondaFragmentToRegTerzaFragment(email, username, password, gender, birthDate)

            Navigation.findNavController(view).navigate(action)
        }

        return view
    }
}