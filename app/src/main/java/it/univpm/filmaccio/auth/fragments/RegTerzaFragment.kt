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
import com.google.firebase.storage.FirebaseStorage
import it.univpm.filmaccio.R
import it.univpm.filmaccio.databinding.FragmentRegTerzaBinding
import it.univpm.filmaccio.main.MainActivity
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
        propicImageView = binding.propicSetImageView

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

    // Questo metodo inserisce l'immagine di profilo scelta dall'utente in una ShapeableImageView che ha una forma rotonda, in questo modo, anche se l'immagine scelta dall'utente sarà rettangolare, verrà visualizzata in modo circolare
    // Piccolo appunto: in tutto il resto dell'app, per visualizzare l'immagine di profilo dell'utente, userò una ShapeableImageView che è un componente di Material Design. Questa ShapeableImageView consente
    private fun loadImageWithCircularCrop(imageUri: Uri?) {
        val requestBuilder = Glide.with(this)
            .asBitmap()
            .load(imageUri)
            .apply(RequestOptions.circleCropTransform())

        val target = object : CustomTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                // Salva l'immagine ritagliata in un file temporaneo
                croppedImageFile = saveBitmapToFile(resource)
                // Carica l'immagine ritagliata in un ImageView
                propicImageView.setImageBitmap(resource)
            }

            override fun onLoadCleared(placeholder: Drawable?) {
                // Rimuove l'immagine caricata in precedenza
                propicImageView.setImageDrawable(null)
            }
        }

        requestBuilder.into(target)
    }

    private fun saveBitmapToFile(bitmap: Bitmap): File {
        val file = File(requireContext().cacheDir, "propic.png")
        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.flush()
        outputStream.close()
        return file
    }

    private fun uploadPropicAndUser(uid: String?) {
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference
        val propicRef = storageRef.child("propic/${uid}.png")
        val imageUri = if (croppedImageFile != null) {
            Uri.fromFile(croppedImageFile)
        } else {
            val drawable = propicImageView.drawable as BitmapDrawable
            val bitmap = drawable.bitmap
            val defaultImageFile = saveBitmapToFile(bitmap)
            Uri.fromFile(defaultImageFile)
        }
        val uploadTask = propicRef.putFile(imageUri)

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
                val downloadUri = task.result
                val imageURL = downloadUri.toString()
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

    private fun addNewUserToFirestore(uid: String?, imageURL: String) {
        val user = hashMapOf(
            "uid" to uid,
            "email" to email,
            "username" to username,
            "gender" to gender,
            "birthDate" to birthDate,
            "nameShown" to nameShown,
            "profileImage" to imageURL
        )
        val followDocument = hashMapOf(
            "followers" to arrayListOf<String>(),
            "following" to arrayListOf()
        )
        val listsDocument = hashMapOf(
            "watchlist_m" to arrayListOf<Long>(),
            "watchlist_t" to arrayListOf(),
            "watched_m" to arrayListOf(),
            "watching_t" to arrayListOf(),
            "favorite_m" to arrayListOf(),
            "favorite_t" to arrayListOf()
        )

        FirestoreService.collectionUsers.document(uid!!)
            .set(user)
            .addOnFailureListener {
                // Si è verificato un errore durante l'aggiunta dell'utente al database Firestore
                Toast.makeText(
                    requireContext(),
                    "Registrazione al database fallita, avvisa il nostro team di supporto",
                    Toast.LENGTH_LONG
                ).show()
            }
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
        FirestoreService.collectionLists.document(uid)
            .set(listsDocument)
            .addOnSuccessListener {
                navigateToHomeActivity()
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

    private fun navigateToHomeActivity() {
        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }
}