package it.univpm.filmaccio.auth.fragments

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import it.univpm.filmaccio.R
import it.univpm.filmaccio.databinding.FragmentRegPrimaBinding
import it.univpm.filmaccio.main.utils.FirestoreService

// Questo fragment è il primo passo della registrazione tramite email, in cui l'utente inserisce email, username e password
class RegPrimaFragment : Fragment() {

    private var _binding: FragmentRegPrimaBinding? = null
    private val binding get() = _binding!!

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
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegPrimaBinding.inflate(inflater, container, false)

        buttonBack = binding.buttonBack
        buttonAvanti = binding.buttonFine
        emailRegInputTextLayout = binding.emailRegInputLayout
        usernameRegInputTextLayout = binding.usernameRegInputLayout
        passwordRegInputTextLayout = binding.passwordRegInputLayout
        passwordConfirmRegInputTextLayout = binding.passwordConfirmRegInputLayout
        emailRegInputTextEdit = binding.emailRegInputTextEdit
        usernameRegInputTextEdit = binding.usernameRegInputTextEdit
        passwordRegInputTextEdit = binding.passwordRegInputTextEdit
        passwordConfirmRegInputTextEdit = binding.passwordConfirmRegInputTextEdit

        // Impostazione del listener sul click per il pulsante per tornare indietro
        buttonBack.setOnClickListener {
            // Navigazione al fragment di login (sempre tramite il solito navController)
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_regPrimaFragment_to_loginFragment)
        }

        // Impostazione del listener sul click per il pulsante per andare avanti con la registrazione
        buttonAvanti.setOnClickListener {
            // Ottenimento dei valori inseriti dall'utente
            val email = emailRegInputTextEdit.text.toString()
            // Il nome utente viene salvato in minuscolo per evitare problemi di case sensitivity, quindi tutti gli username saranno in minuscolo
            val username = usernameRegInputTextEdit.text.toString().lowercase()
            val password = passwordRegInputTextEdit.text.toString()
            val passConfirm = passwordConfirmRegInputTextEdit.text.toString()
            // Rimozione di eventuali errori precedenti nei textLayout (come abbiamo fatto nel Fragment login)
            emailRegInputTextLayout.isErrorEnabled = false
            usernameRegInputTextLayout.isErrorEnabled = false
            passwordRegInputTextLayout.isErrorEnabled = false
            passwordConfirmRegInputTextLayout.isErrorEnabled = false

            // Verifica che l'indirizzo email inserito sia valido
            if (!isEmailValid(email)) {
                // Se non è valido, viene mostrato un errore nel textLayout e si interrompe l'esecuzione del codice
                emailRegInputTextLayout.isErrorEnabled = true
                emailRegInputTextLayout.error = "Indirizzo email non valido"
                return@setOnClickListener
            }

            // Verifica che l'username inserito sia valido
            if (!isUsernameValid(username)) {
                // Se non è valido, viene mostrato un errore nel textLayout e si interrompe l'esecuzione del codice mostrando un messaggio che spiega come deve essere strutturato lo username
                usernameRegInputTextLayout.isErrorEnabled = true
                usernameRegInputTextLayout.error =
                    "Il nome utente deve essere lungo almeno 3 caratteri, deve contenere almeno una lettera e può contenere solo lettere, numeri e i caratteri '.' e '_'"
                return@setOnClickListener
            }

            // Verifica che la password inserita sia valida
            if (!isPasswordValid(password)) {
                // Se non è valida, viene mostrato un errore nel textLayout e si interrompe l'esecuzione del codice mostrando un messaggio che spiega come deve essere strutturata la password
                passwordRegInputTextLayout.isErrorEnabled = true
                passwordRegInputTextLayout.error =
                    "La password deve essere lunga almeno 8 caratteri e contenere almeno una lettera maiuscola, una minuscola e un numero"
                return@setOnClickListener
            }

            // Verifica che la password inserita sia uguale alla password di conferma
            if (password != passConfirm) {
                // Se le due password non corrispondono, viene mostrato un errore nel textLayout e si interrompe l'esecuzione del codice
                passwordConfirmRegInputTextLayout.isErrorEnabled = true
                passwordConfirmRegInputTextLayout.error = "Le password non corrispondono"
                return@setOnClickListener
            }

            // Verifica che l'email inserita non sia già associata ad un account esistente
            isEmailAlreadyRegistered(email) { emailExists ->
                if (emailExists) {
                    // Se l'email è già associata ad un account esistente, viene mostrato un errore nel textLayout e si interrompe l'esecuzione del codice
                    emailRegInputTextLayout.isErrorEnabled = true
                    emailRegInputTextLayout.error =
                        "L'indirizzo email è già associato ad un account esistente"
                } else {
                    // Se l'email non è già associata ad un account esistente, viene verificato che lo username inserito non sia già associato ad un account esistente
                    isUsernameAlreadyRegistered(username) { usernameExists ->
                        if (usernameExists) {
                            // Se lo username è già associato ad un account esistente, viene mostrato un errore nel textLayout e si interrompe l'esecuzione del codice
                            usernameRegInputTextLayout.isErrorEnabled = true
                            usernameRegInputTextLayout.error = "Il nome utente non è disponibile"
                        } else {
                            // Se lo username non è già associato ad un account esistente, viene mostrato un messaggio di successo e si naviga al fragment successivo passando i dati inseriti dall'utente
                            // Quì passiamo i dati inseriti dall'utente (dopo averli validati) tramite l'action creata nel navGraph (fate sempre riferimento al file nav_login.xml in cui ho specificato, per alcune action,
                            // quali dati e di che tipo devono essere passati nella navigazione)
                            // In questo caso passiamo tre stringhe: email, username e password che poi possono essere riottenute nel fragment successivo
                            val action =
                                RegPrimaFragmentDirections.actionRegPrimaFragmentToRegSecondaFragment(
                                    email, username, password
                                )
                            // Quì navighiamo attraverso l'action definita prima al prossimo fragment.
                            // Infatti l'action si riferisce a actionRegPrimaFragmentToRegSecondaFragment che è l'id dell'action che porta dal framgnet RegPrimaFragment al fragment RegSecondaFragment mantenendo i dati necessari
                            Navigation.findNavController(binding.root).navigate(action)
                        }
                    }
                }
            }
            // Questo if serve per fare un return@setOnClickListener dopo che si è verificato che l'email è già associata ad un account esistente o che lo username è già associato ad un account esistente
            if (usernameRegInputTextLayout.isErrorEnabled || emailRegInputTextLayout.isErrorEnabled) {
                return@setOnClickListener
            }
        }
        return binding.root
    }

    // Metodo che si occupa di verificare che l'email inserita sia valida in senso di pattern di email, quindi in poche parole controlla che sia della forma "qualcosa@qualcosa.qualcosa" (se non erro non verifica che l'ultimo qualcosa sia un dominio valido)
    private fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // Metodo che si occupa di verificare se l'indirizzo email inserito sia già associato ad un account esistente nel database FIRESTORE
    private fun isEmailAlreadyRegistered(email: String, callback: (Boolean) -> Unit) {
        // Quì creiamo la query che andrà a verificare se esiste un documento nel database che abbia un campo email uguale a quello inserito dall'utente
        // Nota: per creare la query usiamo la classe FirestoreService che contiene tutti i metodi per interagire con il database FIRESTORE
        val query = FirestoreService.getWhereEqualTo("users", "email", email)
        // Quì aggiungiamo un listener per il successo della query
        query.get().addOnSuccessListener { querySnapshot ->
            // Quì verifichiamo che la query abbia restituito dei risultati, se non ha restituito risultati vuol dire che non esiste un documento nel database con l'email inserita dall'utente
            val exists = !querySnapshot.isEmpty
            // Quì chiamiamo la callback passandogli il risultato della query
            // La callback è una funzione che viene passata come parametro ad un'altra funzione e che viene chiamata all'interno di quest'ultima
            // In altre parole è quel "emailExists" che viene chiamato all'interno della lambda relativa a isEmailAlreadyRegistered dentro il listener del pulsante buttonAvanti
            callback(exists)
        }.addOnFailureListener {
                // Se la query fallisce, probabilmente è dovuto ad un errore di connessione, quindi mostriamo un messaggio di errore
                Toast.makeText(context, "Errore di connessione", Toast.LENGTH_SHORT).show()
                // Poi agiamo come se l'email non esistesse, quindi chiamiamo la callback passandogli false
                // Facciamo così perché essendoci stato un errore di connessione, non possiamo sapere se l'email esiste o meno, quindi per sicurezza assumiamo che non esista e facciamo riprovare l'inserimento all'utente
                callback(true)
            }
    }

    // Metodo che si occupa di verificare che lo username inserito sia valido in senso di pattern di username (scelto da noi), quindi in poche parole controlla che sia della forma giusta
    private fun isUsernameValid(username: String): Boolean {
        // Verifica che il nome utente non contenga caratteri speciali, che abbia una lunghezza minima di 3 caratteri e una lunghezza massima di 30 caratteri, e che gli unici caratteri speciali che può contenere siano _ e .
        // Questo controllo lo fa tramite un pattern regex (molto molto comodo per fare controlli di questo tipo, mi piace un bel po')
        val pattern = "^(?=.*[a-z])[a-z0-9_.]{3,30}$".toRegex()
        // Quì viene restituito il risultato del match tra il pattern e lo username inserito dall'utente
        return pattern.matches(username)
    }

    // Metodo che si occupa di verificare se lo username inserito sia già associato ad un account esistente nel database FIRESTORE, è praticamente uguale a isEmailAlreadyRegistered
    private fun isUsernameAlreadyRegistered(username: String, callback: (Boolean) -> Unit) {
        val query = FirestoreService.getWhereEqualTo("users", "username", username)
        query.get().addOnSuccessListener { querySnapshot ->
                val exists = !querySnapshot.isEmpty
                callback(exists)
            }.addOnFailureListener {
                callback(true)
            }
    }

    // Metodo che si occupa di verificare che la password inserita sia valida in senso di pattern di password scelto da noi
    private fun isPasswordValid(password: String): Boolean {
        // Anche qui viene usato un pattern regex per verificare che la password abbia almeno 8 caratteri, almeno una lettera maiuscola, una minuscola e almeno un numero
        val pattern = Regex("^(?=.*[a-z])(?=.*\\d).{8,}$")
        return password.matches(pattern)
    }
}