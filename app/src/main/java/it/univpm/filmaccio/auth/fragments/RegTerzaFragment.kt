package it.univpm.filmaccio.auth.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Timestamp
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import it.univpm.filmaccio.R
import it.univpm.filmaccio.databinding.FragmentRegTerzaBinding
import it.univpm.filmaccio.main.activities.MainActivity
import it.univpm.filmaccio.main.utils.Constants
import it.univpm.filmaccio.main.utils.FirestoreService
import it.univpm.filmaccio.main.utils.UserUtils
import java.io.File
import java.io.FileOutputStream

// Questo fragment è il terzo e ultimo passo della registrazione tramite email, in cui l'utente inserisce il proprio nome visualizzato e la propria immagine profilo
// Anche quì abbiamo la soppressione del deprecation per l'utilizzo di alcuni metodi in modo da non avere warning "inutili"
@Suppress("DEPRECATION")
class RegTerzaFragment : Fragment() {

    // Per la spiegazione di questo companion object, vedere la classe LoginFragment (il 7 l'ho scelto totalmente a caso)
    companion object {
        private const val PICK_IMAGE_REQUEST = 7
    }

    private var _binding: FragmentRegTerzaBinding? = null
    private val binding get() = _binding!!

    private lateinit var buttonBack: Button
    private lateinit var buttonFine: Button
    private lateinit var nomeVisualizzatoTextInputEditText: TextInputEditText
    private lateinit var nomeVisualizzatoTectInputLayout: TextInputLayout
    private lateinit var propicImageView: ShapeableImageView
    private lateinit var selectedImageUri: Uri
    private lateinit var email: String
    private lateinit var username: String
    private lateinit var password: String
    private lateinit var gender: String
    private lateinit var birthDate: Timestamp
    private lateinit var nameShown: String

    // Variabile che ci serve per salvare temporaneamente l'immagine profilo scelta dall'utente (in modo che sia ritagliata in forma rotonda)
    private var croppedImageFile: File? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegTerzaBinding.inflate(inflater, container, false)

        buttonBack = binding.buttonBack
        buttonFine = binding.buttonFine
        nomeVisualizzatoTextInputEditText = binding.nomeVisualizzatoTextInputEditText
        nomeVisualizzatoTectInputLayout = binding.nomeVisualizatoTextInputLayout
        propicImageView = binding.propicSetSimageView

        // Quì recuperiamo i dati passati dal fragment precedente (RegSecondaFragment) prima rispetto a quando l'abbiamo fatto nel RegSecondaFragment perché ci servirà lo username per inserirlo di default nel campo nome visualizzato
        val args: RegTerzaFragmentArgs by navArgs()
        email = args.email
        username = args.username
        password = args.password
        gender = args.gender
        birthDate = args.birthDate

        // Quì infatti inseriamo lo username nel campo nome visualizzato, così se l'utente vuole avere un nome visualizzato uguale allo username, non deve riscriverlo
        nomeVisualizzatoTextInputEditText.setText(username, TextView.BufferType.EDITABLE)

        // Impostazione del listener sul click per il pulsante per tornare indietro
        buttonBack.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_regTerzaFragment_to_regSecondaFragment)
        }

        // Impostazione del listener sul click per il pulsante per completare la registrazione
        buttonFine.setOnClickListener {
            // Qui resettiamo eventuali errori nel textLayout del nome visualizzato
            nomeVisualizzatoTectInputLayout.isErrorEnabled = false
            // Qui recuperiamo il nome visualizzato inserito dall'utente
            nameShown = nomeVisualizzatoTextInputEditText.text.toString()

            // Qui controlliamo se il nome visualizzato inserito dall'utente non è valido (cioè se non è lungo tra 3 e 50 caratteri)
            if (nameShown.length > 50 || nameShown.isEmpty() || nameShown.length < 3) {
                // Se non è valido, mostriamo un errore nel textLayout del nome visualizzato e interrompiamo l'esecuzione del codice
                nomeVisualizzatoTectInputLayout.isErrorEnabled = true
                nomeVisualizzatoTectInputLayout.error =
                    "Il nome visualizzato deve essere lungo tra 3 e 50 caratteri"
                return@setOnClickListener
            }

            // Se il nome visualizzato è valido, allora ci occupiamo dell'immagine profilo
            // Per farlo, supponiamo che l'immagine profilo inserita dall'utente non dia problemi e procediamo direttamente con la registrazione inserendo l'immagine profilo durante la registrazione stessa
            val auth = UserUtils.auth
            // Chiamiamo il metodo per registrare l'utente con email e password e gli passiamo come parametri email e password inserite dall'utente e ci aggiungiamo un listener in ascolto sul completamento della registrazione
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    // Se la registrazione è andata a buon fine
                    if (task.isSuccessful) {
                        // Recuperiamo l'utente registrato
                        val user = auth.currentUser
                        // Recuperiamo l'uid dell'utente registrato
                        val uid = user?.uid
                        // Inviamo l'email di verifica all'utente registrato e ci aggiungiamo un listener in ascolto sul completamento dell'invio dell'email di verifica
                        user?.sendEmailVerification()
                            ?.addOnCompleteListener { verifyTask ->
                                // Se l'invio dell'email di verifica è andato a buon fine
                                if (verifyTask.isSuccessful) {
                                    // Mostriamo un dialog all'utente per avvisarlo che deve verificare la sua email (potremmo in futuro aggiungere al database un campo che indica
                                    // se l'utente ha verificato la sua email o meno e in base a quello bloccare o meno l'accesso a determinate funzionalità dell'app)
                                    MaterialAlertDialogBuilder(requireContext())
                                        .setTitle("Verifica la tua email")
                                        .setMessage("Benvenuto in Filmaccio! Per sbloccare tutte le funzionalità dell'app, ricordati di verificare il tuo indirizzo email cliccando sul link che ti abbiamo inviato.")
                                        .setPositiveButton("OK") { dialog, _ ->
                                            dialog.dismiss()
                                        }
                                        .show()
                                } else {
                                    // Se l'invio dell'email di verifica non è andato a buon fine, mostriamo un toast all'utente per avvisarlo che l'invio dell'email di verifica è fallito
                                    Toast.makeText(
                                        requireContext(),
                                        "Invio dell'email di verifica fallito, contatta il nostro team di supporto",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        // Chiamiamo il metodo per caricare l'immagine profilo e l'utente nel database e gli passiamo come parametro l'uid dell'utente registrato
                        uploadPropicAndUser(uid)
                    } else {
                        // Se la registrazione non è andata a buon fine, mostriamo un toast all'utente per avvisarlo che la registrazione è fallita e lo reindirizziamo alla schermata di login
                        Toast.makeText(
                            requireContext(),
                            "Registrazione fallita, riprova",
                            Toast.LENGTH_LONG
                        )
                            .show()
                        Navigation.findNavController(binding.root)
                            .navigate(R.id.action_regTerzaFragment_to_loginFragment)
                    }
                }
        }

        // Impostazione del listener sul click per l'immagine profilo
        propicImageView.setOnClickListener { onPropicClick() }

        return binding.root
    }

    // Metodo che viene chiamato quando l'utente clicca sull'immagine profilo da impostare
    private fun onPropicClick() {
        // Creiamo un intent per aprire la galleria in modo da permettere all'utente di scegliere un'immagine profilo
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        // Avviamo l'activity per scegliere un'immagine profilo (qui usiamo il metodo deprecato e usiamo il codice contenuto nel companion object così come abbiamo fatto nel fragment del login)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    // Discorso analogo fatto per onActivityResult nel fragment del login
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            // Se l'utente ha scelto un'immagine profilo correttamente, recuperiamo l'uri dell'immagine profilo scelta
            selectedImageUri = data.data!!
            // Chiamiamo il metodo per caricare l'immagine profilo scelta dall'utente in un ImageView
            loadImageWithCircularCrop(selectedImageUri)
        }
    }

    // Questo metodo inserisce l'immagine di profilo scelta dall'utente nella ShapeableImageView propicImageView dopo averla modificata in modo da renderla circolare e averla salvata in un file temporaneo (in modo da poterla caricare nel database)
    // Se c'è qualcosa che non vi torna in questo metodo (e in quelli che vengono chiamati all'interno) è perché mi sono fatto aiutare da ChatGPT per riuscire a fare questa cosa di ritagliare l'immagine e salvarla. Quindi ci sono alcune cose che non ho capito bene nemmeno io
    // e forse è anche per questo che non sono riuscito a fare in modo che funzionasse anche nella classe RegGoogleSecondaFragment (che è praticamente identica a questa). Ovviamente ChatGPT può sbarellare
    // Piccolo appunto: questo è il metodo incriminato che mi ha dato quel problema strano. Se guardate, infatti, nella classe RegGoogleSecondaFragment, a partire dal metodo onPropicClick() fino alla fine del file, il codice è esattamente identico a quello che trovate qui.
    // Tuttavia, nonostante il codice sia esattamente identico, nell'altra classe non funziona. Ho debuggato line-by-line e ho scoperto che il problema è proprio qui, in questo metodo. Infatti, quando il codice passa alla riga 213, successivamente in questa classe si passa al metodo onResourceReady, mentre nell'altra classe si passa al metodo onLoadFailed e non so perché.
    private fun loadImageWithCircularCrop(imageUri: Uri?) {
        // Qui usiamo Glide (una libreria per gestire le immagini in Android in modo da caricarle facilmente da internet e modificarle nel codice e altro) per creare una RequestBuilder che ci permette di caricare l'immagine profilo scelta dall'utente dopo averla modificata (ritagliandola in modo circolare)
        val requestBuilder = Glide.with(this)
            .asBitmap()
            .load(imageUri)
            .apply(RequestOptions.circleCropTransform())

        // Con questo codice qui sotto, creiamo un CustomTarget che ci permette di caricare l'immagine profilo modificata in un file temporaneo attraverso il metodo saveBitmapToFile e di caricarla nella ShaepableImageView
        // Sinceramente non so spiegare bene perché viene fatto in questo modo.
        val target = object : CustomTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                // Salviamo l'immagine ritagliata in un file temporaneo
                croppedImageFile = saveBitmapToFile(resource)
                // Carica l'immagine ritagliata nella ShapeableImageView
                propicImageView.setImageBitmap(resource)
            }

            // Questo metodo è una delle cose che non so spiegare (come ho scritto sopra). Credo che possa servire a eliminare l'immagine contenuta nella ShapeableImageView per qualche motivo.
            override fun onLoadCleared(placeholder: Drawable?) {
                // Rimuoviamo l'immagine caricata in precedenza
                propicImageView.setImageDrawable(null)
            }
        }

        // Carichiamo l'immagine profilo modificata nella ShapeableImageView dopo averla ritagliata e salvata in un file temporaneo
        requestBuilder.into(target)
    }

    // Questo metodo serve a salvare l'immagine profilo ritagliata in un file temporaneo
    private fun saveBitmapToFile(bitmap: Bitmap): File {
        // Creiamo un file temporaneo chiamato propic.jpg nella cache dell'applicazione
        val file = File(requireContext().cacheDir, "propic.jpg")
        // Creiamo un FileOutputStream per poter scrivere nel file
        val outputStream = FileOutputStream(file)
        // Comprimiamo l'immagine profilo ritagliata in modo da ridurne la dimensione
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.flush()
        outputStream.close()
        return file
    }

    // Questo metodo serve a caricare l'immagine profilo ritagliata in Firebase Cloud Storage e i dati dell'utente in Firestore
    private fun uploadPropicAndUser(uid: String?) {
        // Creiamo un riferimento al Cloud Storage di Firebase
        val storageRef = Firebase.storage.reference
        // Creiamo un riferimento al file che vogliamo caricare nel Cloud Storage
        val propicRef = storageRef.child("propic/${uid}.jpg")
        // Creiamo un Uri per poter caricare l'immagine profilo ritagliata nel Cloud Storage
        val imageUri = if (croppedImageFile != null) {
            // Se l'immagine profilo è stata modificata dall'utente, carichiamo l'immagine profilo ritagliata
            Uri.fromFile(croppedImageFile)
        } else {
            // Se l'immagine profilo non è stata modificata dall'utente, carichiamo l'immagine profilo di default
            val drawable = propicImageView.drawable as BitmapDrawable
            val bitmap = drawable.bitmap
            val defaultImageFile = saveBitmapToFile(bitmap)
            Uri.fromFile(defaultImageFile)
        }
        // Creiamo una task per caricare l'immagine profilo nel Cloud Storage
        val uploadTask = propicRef.putFile(imageUri)

        // Facciamo continuare la task per verificare se ci sono problemi e poi aggiungiamo un listener al completamento della task
        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                // Si è verificato un errore durante il caricamento dell'immagine
                val exception = task.exception
                Toast.makeText(
                    requireContext(),
                    "Errore durante il caricamento dell'immagine, avvisa il nostro team di supporto (Errore: ${exception.toString()})",
                    Toast.LENGTH_LONG
                )
                    .show()
            }
            // Ottiene l'URL del download dell'immagine
            propicRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Se il caricamento dell'immagine è andato a buon fine, otteniamo l'URI di riferimento dell'immagine caricata in Cloud Storage con task.result che si riferisce a propicRef.downloadUrl di qualche riga sopra
                val downloadUri = task.result
                val imageURL = downloadUri.toString()
                // Qui chiamiamo il metodo per aggiungere i dati dell'utente a Firestore in modo da completare la registrazione
                addNewUserToFirestore(uid, imageURL)
            } else {
                // Si è verificato un errore durante il caricamento dell'immagine
                val exception = task.exception
                Toast.makeText(
                    requireContext(),
                    "Errore durante il caricamento dell'immagine, avvisa il nostro team di supporto (Errore: ${exception.toString()})",
                    Toast.LENGTH_LONG
                )
                    .show()
            }
        }
    }

    // Questo metodo serve ad aggiungere i dati dell'utente a Firestore in modo da completare la registrazione
    private fun addNewUserToFirestore(uid: String?, imageURL: String) {
        // Creiamo un HashMap con i dati dell'utente
        val user = hashMapOf(
            "uid" to uid,
            "email" to email,
            "username" to username,
            "gender" to gender,
            "birthDate" to birthDate,
            "nameShown" to nameShown,
            "profileImage" to imageURL,
            "backdropImage" to Constants.DESERT_BACKDROP_URL
        )
        // Creiamo una HashMap con due ArrayList vuoti per setuppare il documento follow del nuovo utente
        val followDocument = hashMapOf(
            "followers" to arrayListOf<String>(),
            "following" to arrayListOf(),
            "people" to arrayListOf()
        )
        // Creiamo una HashMap con sei ArrayList vuoti per setuppare il documento lists del nuovo utente
        val listsDocument = hashMapOf(
            "watchlist_m" to arrayListOf<Long>(),
            "watchlist_t" to arrayListOf(),
            "watched_m" to arrayListOf(),
            "watching_t" to arrayListOf(),
            "favorite_m" to arrayListOf(),
            "favorite_t" to arrayListOf(),
            "finished_t" to arrayListOf()
        )
        val episodesDocument = hashMapOf(
            "watchingSeries" to hashMapOf<String, Any>()
        )
        val reviewsDocument = hashMapOf(
            "movies" to arrayListOf<List<String>>(),
            "series" to arrayListOf()
        )

        // A tal proposito, forse adesso è il momento migliore di spiegare come ho strutturato il database Firestore.
        // Abbiamo una collection users, una collection follow e una collection lists.
        // Tutte queste collection contengono un documento per ogni utente registrato all'app.
        // I documenti relativi agli utenti hanno sempre come nome l'uid dell'utente corrispondente.
        // Il documento dell'utente dentro users contiene i dati dell'utente come sono nella HashMap che abbiamo creato sopra.
        // Il documento dell'utente dentro follow contiene due ArrayList: una per i follower dell'utente e una per gli utenti che l'utente segue (ovviamente sono entrambe inizialmente vuote).
        // Il documento dell'utente dentro lists contiene sei ArrayList: una per i film da vedere (watchlist_m), una per le serie TV da vedere (watchlist_t), una per i film visti (watched_m),
        // una per le serie TV che si sta guardando (watching_t), una per i film preferiti (favorite_m) e una per le serie TV preferite (favorite_t).
        // Per quanto riguarda le liste di film e serie TV, ogni elemento delle liste è un Long che rappresenta l'id del film o della serie TV. Inoltre, il nome delle liste finisce con _m o _t a seconda che la lista sia di film o di serie TV.
        // Questa cosa dei nomi è necessaria perché purtroppo l'API che usiamo non distingue gli id dei film da quelli delle serie TV, quindi passando un'id di un film alla chiamata che ti da i dettagli di una serie TV, non darà errore 404 ma darà i dettagli di una serie TV "a caso".
        // Quindi poi come vedrete nel codice successivo quando si va a lavorare con le liste, useremo l'attributo last() per prendere l'ultimo carattere del nome della lista e capire se è una lista di film o di serie TV e in base a quello faremo la chiamata giusta all'API.
        // Questo ovviamente andrà fatto anche per le eventuali nuove liste create dall'utente. Infatti l'utente, oltre a scegliere il nome della sua lista (che non dovrà superare i 12 caratteri), dovrà anche scegliere se la lista sarà di film o di serie TV e in base a quello nell'aggiunta
        // della lista al database Firestore, si aggiungerà alla collection lists un nuovo campo con il nome della lista e il suffisso _m o _t a seconda che la lista sia di film o di serie TV.


        // Aggiungiamo i dati dell'utente al documento dell'utente dentro la collection users
        FirestoreService.collectionUsers.document(uid!!) // Questo .document(uid!!) serve per creare un documento con nome uguale all'uid dell'utente
            .set(user) // Questo .set(user) serve per aggiungere i dati dell'utente al documento appena creato
            .addOnFailureListener {
                // Si è verificato un errore durante l'aggiunta dell'utente al database Firestore
                Toast.makeText(
                    requireContext(),
                    "Registrazione al database fallita, avvisa il nostro team di supporto",
                    Toast.LENGTH_LONG
                ).show()
            }
        // Aggiungiamo i dati del documento follow del nuovo utente alla collection follow
        FirestoreService.collectionFollow.document(uid)
            .set(followDocument)
            .addOnFailureListener {
                // Si è verificato un errore durante l'aggiunta del documento follow al database Firestore
                Toast.makeText(
                    requireContext(),
                    "Registrazione al database fallita, avvisa il nostro team di supporto",
                    Toast.LENGTH_LONG
                ).show()
            }
        FirestoreService.collectionEpisodes.document(uid)
            .set(episodesDocument)
            .addOnFailureListener {
                Toast.makeText(
                    requireContext(),
                    "Registrazione al database fallita, avvisa il nostro team di supporto",
                    Toast.LENGTH_LONG
                ).show()
            }
        FirestoreService.collectionUsersReviews.document(uid)
            .set(reviewsDocument)
            .addOnFailureListener {
                Toast.makeText(
                    requireContext(),
                    "Registrazione al database fallita, avvisa il nostro team di supporto",
                    Toast.LENGTH_LONG
                ).show()
            }
        // Aggiungiamo i dati del documento lists del nuovo utente alla collection lists
        FirestoreService.collectionLists.document(uid)
            .set(listsDocument)
            .addOnSuccessListener {
                // A questo punto la registrazione è completata, quindi possiamo navigare alla HomeActivity
                navigateToMainActivity()
            }
            .addOnFailureListener {
                // Si è verificato un errore durante l'aggiunta del documento lists al database Firestore
                Toast.makeText(
                    requireContext(),
                    "Registrazione al database fallita, avvisa il nostro team di supporto",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    // Questo metodo serve per navigare alla HomeActivity, viene chiamato quando la registrazione è completata
    private fun navigateToMainActivity() {
        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }
}