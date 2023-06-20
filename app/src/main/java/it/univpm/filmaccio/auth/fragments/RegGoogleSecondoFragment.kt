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
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Timestamp
import com.google.firebase.storage.FirebaseStorage
import it.univpm.filmaccio.R
import it.univpm.filmaccio.databinding.FragmentRegGoogleSecondoBinding
import it.univpm.filmaccio.main.MainActivity
import it.univpm.filmaccio.main.utils.FirestoreService
import it.univpm.filmaccio.main.utils.UserUtils
import java.io.File
import java.io.FileOutputStream

@Suppress("DEPRECATION")
class RegGoogleSecondoFragment : Fragment() {

    companion object {
        private const val PICK_IMAGE_REQUEST = 8
    }

    private var _binding: FragmentRegGoogleSecondoBinding? = null
    private val binding get() = _binding!!

    private lateinit var buttonBack: Button
    private lateinit var buttonFine: Button
    private lateinit var nomeVisualizzatoTextInputEditText: TextInputEditText
    private lateinit var nomeVisualizzatoTectInputLayout: TextInputLayout
    private lateinit var propicImageView: ShapeableImageView
    private lateinit var selectedImageUri: Uri
    private lateinit var username: String
    private lateinit var gender: String
    private lateinit var birthDate: Timestamp
    private lateinit var nameShown: String
    private lateinit var email: String
    private var croppedImageFile: File? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegGoogleSecondoBinding.inflate(inflater, container, false)

        buttonBack = binding.buttonBack
        buttonFine = binding.buttonFine
        nomeVisualizzatoTextInputEditText = binding.nomeVisualizzatoTextInputEditText
        nomeVisualizzatoTectInputLayout = binding.nomeVisualizatoTextInputLayout
        propicImageView = binding.propicSetImageView

        val args: RegGoogleSecondoFragmentArgs by navArgs()
        username = args.username
        gender = args.gender
        birthDate = args.birthDate

        nomeVisualizzatoTextInputEditText.setText(username, TextView.BufferType.EDITABLE)

        buttonBack.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_regGoogleSecondoFragment_to_regGooglePrimoFragment)
        }

        buttonFine.setOnClickListener {
            nomeVisualizzatoTectInputLayout.isErrorEnabled = false
            nameShown = nomeVisualizzatoTextInputEditText.text.toString()

            if (nameShown.length > 50 || nameShown.isEmpty() || nameShown.length < 3) {
                nomeVisualizzatoTectInputLayout.isErrorEnabled = true
                nomeVisualizzatoTectInputLayout.error =
                    "Il nome visualizzato deve essere lungo tra 3 e 50 caratteri"
                return@setOnClickListener
            }

            val firebaseUser = UserUtils.auth.currentUser
            email = firebaseUser?.email.toString()
            val uid = firebaseUser?.uid
            uploadPropicAndUser(uid)
        }

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