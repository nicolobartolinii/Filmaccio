package it.univpm.filmaccio.auth.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import it.univpm.filmaccio.R
import it.univpm.filmaccio.databinding.FragmentLoginBinding
import it.univpm.filmaccio.main.activities.MainActivity
import it.univpm.filmaccio.main.utils.FirestoreService
import it.univpm.filmaccio.main.utils.UserUtils

/**
 * Questo fragment gestisce il login con email e password e il login con Google e permette di registrarsi.
 *
 * Questo fragment si raggiunge se l'utente, nell'entry point dell'app, non risulta loggato.
 *
 * @see it.univpm.filmaccio.auth.AuthActivity
 * @see UserUtils
 * @see FirestoreService
 * @see RegPrimaFragment
 * @see RegGooglePrimoFragment
 * @see PasswordResetFragment
 * @see MainActivity
 *
 * @author nicolobartolinii
 */
@Suppress("DEPRECATION")
class LoginFragment : Fragment() {

    // Costante che contiene il codice di richiesta per il login con Google che serve per lanciare
    // l'activity di login con Google e per gestire il risultato di quell'activity specifica
    // Questo codice è arbitrario e può essere qualsiasi numero, utilizzando questo codice noi
    // lanciamo l'activity di login/registrazione con Google e poi quando attendiamo il risultato
    // catturiamo il risultato con questo codice e lo gestiamo
    companion object {
        private const val RC_GOOGLE_SIGN_IN = 123 // Numero scelto in modo arbitrario
    }

    // Inizializziamo il View Binding, che serve per accedere agli elementi dell'interfaccia
    // in modo molto più semplice e performante rispetto al classico findViewById
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    // Variabili che contengono i riferimenti agli elementi dell'interfaccia
    private lateinit var regEmailButton: Button
    private lateinit var buttonEntra: Button
    private lateinit var inputEmailLayout: TextInputLayout
    private lateinit var inputEmail: TextInputEditText
    private lateinit var inputPasswordLayout: TextInputLayout
    private lateinit var inputPassword: TextInputEditText
    private lateinit var buttonEntraGoogle: Button
    private lateinit var buttonForgotPassword: Button

    // Variabile che contiene il riferimento all'istanza di FirebaseAuth
    private lateinit var auth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflating del layout di questo fragment (verrà fatto in tutte le schermate quindi non lo commenterò più,
        // serve per "disegnare" l'interfaccia ed è il riferimento all'XML della schermata, senza questo la schermata sarebbe vuota.
        // Questo riferimento avviene attraverso la classe FragmentLoginBinding che è generata automaticamente dal View Binding)
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        // Inizializzazione delle variabili che contengono i riferimenti agli elementi dell'interfaccia
        regEmailButton = binding.buttonRegEmail
        buttonEntra = binding.buttonEntra
        inputEmailLayout = binding.inputLoginUserEmail
        inputEmail = binding.inputUserEmailEditText
        inputPasswordLayout = binding.inputLoginPassword
        inputPassword = binding.inputPasswordEditText
        buttonEntraGoogle = binding.buttonEntraGoogle
        buttonForgotPassword = binding.buttonForgotPassword

        // Impostazione del listener del click per il pulsante di registrazione con email
        regEmailButton.setOnClickListener {
            // Navigazione verso il fragment di registrazione con email (RegPrimaFragment)
            // La navigazione avviene tramite il Navigation Controller che è definito nel file di navigazione (res/navigation/nav_login.xml)
            // In quel file sono definiti tutti i collegamenti tra i vari fragment della AuthActivity
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_loginFragment_to_regPrimaFragment)
        }

        // Inizializzazione dell'istanza di FirebaseAuth
        auth = UserUtils.auth

        // Impostazione del listener del click per il pulsante di login con email
        buttonEntra.setOnClickListener {
            // Rimozione di eventuali errori sui campi di input
            inputEmailLayout.isErrorEnabled = false
            inputPasswordLayout.isErrorEnabled = false
            // Ottenimento dell'email e della password inserite dall'utente
            val email = inputEmail.text.toString()
            val password = inputPassword.text.toString()

            // Chiamata del metodo che si occupa di effettuare il login con email e
            // password ed effettuare i vari controlli necessari (solo se i campi non sono vuoti)
            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUserWithEmailAndPassword(email, password)
            }
        }

        // Impostazione del listener del click per il pulsante di login con Google
        buttonEntraGoogle.setOnClickListener {
            // Chiamata del metodo che si occupa di effettuare il login o la registrazione con Google
            signInWithGoogle()
        }

        // Impostazione del listener del click per il pulsante di password dimenticata
        buttonForgotPassword.setOnClickListener {
            // Navigazione verso il fragment di reset della password (PasswordResetFragment)
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_loginFragment_to_passwordResetFragment)
        }
        return binding.root
    }

    // Metodo che viene chiamato quando si clicca sul pulsante di login con email e password
    // Questo metodo si occupa di effettuare i vari controlli necessari e, se non ci sono problemi,
    // di effettuare il login con email e password (ovviamente tutto tramite Firebase Auth)
    private fun loginUserWithEmailAndPassword(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Se il login è andato a buon fine allora viene avviata l'activity principale (MainActivity)
                    startMainActivity()
                } else {
                    // Se il login non è andato a buon fine allora viene i campi di input vengono evidenziati in rosso e viene mostrato un messaggio di errore
                    inputEmailLayout.isErrorEnabled = true
                    inputEmailLayout.error = "Indirizzo email o password errati"
                    inputPasswordLayout.isErrorEnabled = true
                    // Questo testo di errore è vuoto perché altrimenti il testo di errore sarebbe mostrato in alto e in basso
                    inputPasswordLayout.error = " "
                }
            }
    }

    // Metodo che viene chiamato quando si clicca sul pulsante di login con Google
    private fun signInWithGoogle() {
        // Configurazione dell'oggetto GoogleSignInOptions che serve per configurare il login con Google
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()

        // Creazione del client per il login con Google
        val googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        // Avvio dell'activity per il login con Google (qui usiamo il codice RC_GOOGLE_SIGN_IN
        // inizializzato nel companion object per identificare l'activity di login con Google)
        // All'interno di questa activity viene mostrata la schermata di login con Google fornita
        // da firebase dove vengono ottenuti i dati dell'account Google attualmente loggato
        // nel telefono (oppure i dati immessi sul momento in quella schermata)
        // e poi viene chiamato il metodo onActivityResult
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN)
    }

    // Questo è il metodo che viene automaticamente chiamato quando l'activity avviata con startActivityForResult termina
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Controllo che il codice di richiesta sia quello che abbiamo impostato nel companion object (RC_GOOGLE_SIGN_IN)
        // Il companion object è necessario dal momento che se in questo fragment avessimo altri startActivityForResult
        // che si occupano di fare altre cose, ognuna delle cose diverse dovrebbe avrebbe un codice diverso
        // in modo da poterle distinguere quando terminano.
        // Allora all'interno di questo metodo qui, bisogna capire quale delle cose diverse è terminate
        // e per farlo ci serve il corrispondente codice
        // In questo caso specifico abbiamo solo uno startActivityForResult, quindi potenzialmente non servirebbe
        // usare il codice, però per sicurezza e per possibili futuri ampliamenti è comunque meglio usarlo
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            // Ottenimento del task di login con Google
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Ottenimento dell'account Google
                val account = task.getResult(ApiException::class.java)

                // Autenticazione con Google tramite Firebase Authentication
                val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
                UserUtils.auth.signInWithCredential(credential).addOnCompleteListener { authTask ->
                    // Controllo che l'autenticazione con Google tramite Firebase Authentication sia andata a buon fine
                    if (authTask.isSuccessful) {
                        // Se l'autenticazione è andata a buon fine, allora otteniamo la variabile user che contiene
                        // i dati dell'utente appena loggato (anche se non era ancora registrato)
                        val user = UserUtils.auth.currentUser

                        // Qui controlliamo se l'utente è già registrato nel database FIRESTORE
                        // Non stiamo controllando se l'utente è registrato nel database AUTHENTICATION,
                        // ma nel database FIRESTORE. Perché? Perché il database AUTHENTICATION contiene
                        // solo i dati di autenticazione (email, password, uid), mentre il database
                        // FIRESTORE contiene i dati dell'utente (nome utente, nome visualizzato, ecc.)
                        // Inoltre, quando un utente che non è mai entrato nell'app si logga con
                        // Google, viene automaticamente registrato nel database AUTHENTICATION,
                        // ma non nel database FIRESTORE, quindi in quel caso dobbiamo accorgercene
                        // e passare alle schermate di registrazione con Google in cui l'utente aggiunge i
                        // suoi dati e viene inserito nel database FIRESTORE
                        val userRef =
                            FirestoreService.getWhereEqualTo("users", "uid", user!!.uid)

                        // La variabile userRef non è altro che una TASK che si occupa di fare una ricerca nel database.
                        // Qui sotto aggiungiamo a questa TASK un listener che si accorge di quando la task termina
                        userRef.get().addOnCompleteListener { userTask ->
                            // Qui controlliamo se la task è andata a buon fine e se ha trovato
                            // qualcosa (significa che l'utente è già registrato nel database FIRESTORE
                            // e allora possiamo farlo andare alla MainActivity perché si è già registrato precedentemente)
                            if (userTask.isSuccessful && !userTask.result.isEmpty) {
                                // L'utente esiste nel database, passa alla MainActivity
                                startMainActivity()
                            } else {
                                // L'utente non esiste nel database, passa alla registrazionetramite Google
                                Navigation.findNavController(requireView())
                                    .navigate(R.id.action_loginFragment_to_regGooglePrimoFragment)
                            }
                        }
                    } else {
                        // Qui entriamo nell'else relativo al FirebaseAuth.getInstance().signInWithCredential(credential)
                        // Se c'è stato qualche problema con l'autenticazione con Google tramite
                        // Firebase Authentication, mostriamo un messaggio di errore. In questo caso se c'è stato qualche problema
                        // non riguarda il nostro database, quindi non abbiamo altra scelta che
                        // mostrare un messaggio di errore perché non dipende da noi
                        Toast.makeText(
                            requireContext(),
                            "Errore durante l'accesso con Google (Errore: ${task.exception.toString()})",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            } catch (e: ApiException) {
                // Qui c'è qualche altro problema riguardante FirebaseAuth o GoogleSignIn,
                // quindi mostriamo un messaggio di errore (anche qui non dipende da noi)
                Toast.makeText(
                    requireContext(),
                    "Errore durante l'accesso con Google (Errore: ${task.exception.toString()})",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // Questo metodo permette di passare alla MainActivity
    private fun startMainActivity() {
        // Creiamo un intent che si riferisce alla MainActivity
        val intent = Intent(requireContext(), MainActivity::class.java)
        // Avviamo l'activity
        startActivity(intent)
        // Chiudiamo l'activity corrente (AuthActivity)
        requireActivity().finish()
    }
}