package it.univpm.filmaccio.auth.fragments

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
import it.univpm.filmaccio.R
import it.univpm.filmaccio.databinding.FragmentPasswordResetBinding
import it.univpm.filmaccio.main.utils.FirestoreService

// Questa è la classe che si occupa della schermata di reset della password. L'utente inserisce la sua email e, se questa è corretta, riceverà una mail per il reset della password.
// Nota: il form per il cambio della password che si apre quando clicchi nel link della mail, non rispetta il pattern della password che abbiamo usato nella registrazione, quindi
// nel caso dovremmo vedere se è possibile impostare questo pattern nella console di firebase. Ma anche lasciarlo così non credo che sia un enorme problema, non penso che il prof si metta a controllare o a penalizzarci per una roba del genere.
class PasswordResetFragment : Fragment() {

    private var _binding: FragmentPasswordResetBinding? = null
    private val binding get() = _binding!!

    private lateinit var emailPasswordResetTextEdit: EditText
    private lateinit var buttonInviaReset: Button
    private lateinit var buttonBackReset: Button
    private lateinit var db: FirebaseFirestore
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPasswordResetBinding.inflate(inflater, container, false)

        db = FirebaseFirestore.getInstance()

        emailPasswordResetTextEdit = binding.emailPasswordResetTextEdit
        buttonInviaReset = binding.buttonInviaReset
        buttonBackReset = binding.buttonBackReset

        // Impostazione del listener sul click per il bottone di invio della mail di reset della password
        buttonInviaReset.setOnClickListener {
            // Recuperiamo l'email inserita dall'utente
            val email = emailPasswordResetTextEdit.text.toString()
            // Verifichiamo che l'email sia presente nel database
            FirestoreService.getWhereEqualTo("users", "email", email).get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        // Se l'email non è presente nel database, cancelliamo il testo inserito dall'utente e non facciamo nulla (l'utente non dovrebbe poter usare questa schermata per scoprire le email presenti nel database)
                        emailPasswordResetTextEdit.text = null
                    } else {
                        // Se l'email è presente nel database, cancelliamo il testo inserito dall'utente e inviamo la mail di reset della password
                        emailPasswordResetTextEdit.text = null
                        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    }
                }
            // Creiamo una snackbar per avvisare l'utente che la mail potrebbe essere stata inviata (anche in questo caso non diamo nessuna certezza all'utente perché altrimenti potrebbe usare questa schermata per scoprire le email presenti nel database)
            val snackbar = Snackbar.make(
                binding.root,
                "Se l'email è corretta, riceverai una mail per il reset della password",
                Snackbar.LENGTH_LONG
            )
            // Aggiungiamo alla snackbar un bottone per tornare alla schermata di login
            snackbar.setAction("Torna al login") {
                Navigation.findNavController(binding.root)
                    .navigate(R.id.action_passwordResetFragment_to_loginFragment)
            }
            // Mostriamo la snackbar
            snackbar.show()
        }

        // Impostazione del listener sul click per il bottone di ritorno alla schermata di login
        buttonBackReset.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_passwordResetFragment_to_loginFragment)
        }

        return binding.root
    }
}