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
import androidx.navigation.fragment.navArgs
import com.google.firebase.Timestamp
import it.univpm.filmaccio.R
import it.univpm.filmaccio.databinding.FragmentRegSecondaBinding
import java.util.Calendar

// Questo fragment è il secondo passo della registrazione tramite email, in cui l'utente inserisce il proprio genere e la propria data di nascita
class RegSecondaFragment : Fragment() {

    private var _binding: FragmentRegSecondaBinding? = null
    private val binding get() = _binding!!

    private lateinit var buttonBack: Button
    private lateinit var buttonAvanti: Button
    private lateinit var genereRadioGroup: RadioGroup
    private lateinit var dataNascitaDatePicker: DatePicker
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegSecondaBinding.inflate(inflater, container, false)

        buttonBack = binding.buttonBack
        buttonAvanti = binding.buttonFine
        // Per l'inserimento del genere abbiamo un RadioGroup, in cui l'utente può selezionare un solo RadioButton (maschile, femminile o altro)
        genereRadioGroup = binding.genereRadioGroup
        // Per l'inserimento della data di nascita abbiamo un DatePicker in modalità spinner, in cui l'utente può selezionare giorno, mese e anno
        dataNascitaDatePicker = binding.spinnerData

        // Impostazione del listener sul click per il pulsante per tornare indietro
        buttonBack.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_regSecondaFragment_to_regPrimaFragment)
        }

        // Impostazione del listener sul click per il pulsante per andare avanti
        buttonAvanti.setOnClickListener {
            // Otteniamo il genere selezionato dall'utente con un when che controlla quale RadioButton è selezionato
            // Se è selezionato il RadioButton maschile, il genere è "M" (maschile), se è selezionato il RadioButton femminile, il genere è "F" (femminile), altrimenti il genere è "A" (altro)
            // Quindi il genere sarà una stringa che può essere "M", "F" o "A" oppure null se nessun RadioButton è selezionato
            val gender = when (genereRadioGroup.checkedRadioButtonId) {
                R.id.maschileRadioButton -> "M"
                R.id.femminileRadioButton -> "F"
                R.id.altroGenereRadioButton -> "A"
                else -> null
            }

            // Otteniamo la data di nascita selezionata dall'utente inserendo anno, mese e giorno in variabili separate di tipo Int
            val day = dataNascitaDatePicker.dayOfMonth
            val month = dataNascitaDatePicker.month
            val year = dataNascitaDatePicker.year

            // Se l'utente non ha selezionato nessun genere, mostriamo un Toast che lo avvisa di selezionare un genere e interrompiamo l'esecuzione del codice
            if (gender == null) {
                Toast.makeText(requireContext(), "Seleziona un genere", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // Quì creiamo un oggetto Calendar, che ci permette di ottenere la data attuale in modo da verificare poi che l'utente abbia inserito una data di nascita valida (almeno 14 anni)
            // Ho messo come data minima 14 anni giusto per avere un controllo sulla data di nascita e flexare, però possiamo tranquillamente cambiare la variabile minimumAge per impostare un'età minima diversa
            val calendar = Calendar.getInstance()
            val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
            val currentMonth = calendar.get(Calendar.MONTH)
            val currentYear = calendar.get(Calendar.YEAR)

            val minimumAge = 14

            // Quì calcoliamo l'età dell'utente in base alla data di nascita inserita
            var age = currentYear - year

            // Se il mese di nascita è maggiore del mese attuale, l'utente non ha ancora compiuto gli anni, quindi dobbiamo sottrarre 1 all'età
            if (currentMonth < month || (currentMonth == month && currentDay < day)) {
                age--
            }

            // Quì controlliamo che l'età sia maggiore o uguale a minimumAge
            val isValidAge = age >= minimumAge

            // Se l'età non è valida, mostriamo un Toast che avvisa l'utente di inserire una data di nascita valida e interrompiamo l'esecuzione del codice
            if (!isValidAge) {
                Toast.makeText(
                    requireContext(),
                    "Inserisci una data di nascita valida (almeno $minimumAge anni)",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            // Qui settiamo il calendario creato in precedenza con la data di nascita inserita dall'utente (nota: arriviamo a questo codice solo se è tutto ok)
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, day)

            // Quì creiamo un oggetto Timestamp, che ci permette di salvare la data di nascita in formato Timestamp, che è il formato che Firebase utilizza per salvare le date, in questo modo potremo salvare la data in FIRESTORE
            val birthDate = Timestamp(calendar.time)

            // Quì recuperiamo i dati inseriti dall'utente nel primo passo della registrazione (email, username e password) tramite gli argomenti passati dal primo fragment
            // Non ci sono serviti fino ad ora perché stavamo ancora verificando i dati di questo fragment, però a questo punto abbiamo tutti i dati necessari per passare al terzo passo della registrazione
            // Quindi dobbiamo recuperare email, username e password e passarli al terzo fragment insieme al genere e alla data di nascita ottenuti in questo passo della registrazione
            val args: RegSecondaFragmentArgs by navArgs()
            val email: String = args.email
            val username: String = args.username
            val password: String = args.password

            // Per passare i dati al terzo fragment, facciamo così come abbiamo fatto per passare i dati al secondo fragment, creiamo un'azione con i dati che vogliamo passare e poi navighiamo verso l'azione
            val action = RegSecondaFragmentDirections.actionRegSecondaFragmentToRegTerzaFragment(
                email,
                username,
                password,
                gender,
                birthDate
            )

            Navigation.findNavController(binding.root).navigate(action)
        }

        return binding.root
    }
}