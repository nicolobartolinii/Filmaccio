package it.univpm.filmaccio.auth.fragments

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Timestamp
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import it.univpm.filmaccio.R
import it.univpm.filmaccio.databinding.FragmentRegGoogleSecondoBinding
import it.univpm.filmaccio.main.activities.MainActivity
import it.univpm.filmaccio.main.utils.Constants
import it.univpm.filmaccio.main.utils.FirestoreService
import it.univpm.filmaccio.main.utils.UserUtils
import java.io.ByteArrayOutputStream

/**
 * Questo fragment è il secondo passo della registrazione tramite Google, in cui l'utente inserisce
 * il proprio nome visualizzato e la propria immagine profilo (disabilitato per problemi tecnici).
 *
 * Questo passaggio è praticamente identico al terzo passo della registrazione tramite email,
 * quindi per i commenti si rimanda a RegTerzaFragment.
 *
 * @see RegTerzaFragment
 *
 * L'unica differenza sostanziale è che qui l'utente non viene creato in FirebaseAuth, perché in questo caso l'utente è già
 * stato creato come spiegato in LoginFragment. Quindi qui l'utente viene creato solo in Firestore.
 *
 * @author nicolobartolinii
 */
class RegGoogleSecondoFragment : Fragment() {

    // (DISABILITATO per problemi tecnici)
    /*companion object {
        private const val PICK_IMAGE_REQUEST = 8
    }*/

    private var _binding: FragmentRegGoogleSecondoBinding? = null
    private val binding get() = _binding!!

    private lateinit var buttonBack: Button
    private lateinit var buttonFine: Button
    private lateinit var nomeVisualizzatoTextInputEditText: TextInputEditText
    private lateinit var nomeVisualizzatoTectInputLayout: TextInputLayout
    private lateinit var propicImageView: ShapeableImageView

    // (DISABILITATO per problemi tecnici)
    // private lateinit var selectedImageUri: Uri
    private lateinit var email: String
    private lateinit var username: String
    private lateinit var gender: String
    private lateinit var birthDate: Timestamp
    private lateinit var nameShown: String

    // (DISABILITATO per problemi tecnici)
    // private var croppedImageFile: File? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegGoogleSecondoBinding.inflate(inflater, container, false)

        buttonBack = binding.buttonBack
        buttonFine = binding.buttonFine
        nomeVisualizzatoTextInputEditText = binding.nomeVisualizzatoTextInputEditText
        nomeVisualizzatoTectInputLayout = binding.nomeVisualizatoTextInputLayout
        propicImageView = binding.propicSetSimageView

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

    private fun onPropicClick() {
        MaterialAlertDialogBuilder(requireContext()).setTitle("Info di servizio").setMessage(
            "Funzionalità temporaneamente disabilitata per problemi tecnici.\n\n" + "Per personalizzare la tua immagine di profilo, puoi farlo dopo la registrazione, nella schermata di modifica del profilo."
        ).setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }.show()
        /* (DISABILITATO per problemi tecnici)
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)*/
    }

    // (DISABILITATO per problemi tecnici)
    /* @Deprecated("Deprecated in Java")
     override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
         super.onActivityResult(requestCode, resultCode, data)

         if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
             selectedImageUri = data.data!!
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
                 croppedImageFile = saveBitmapToFile(resource)
                 propicImageView.setImageBitmap(resource)
             }

             override fun onLoadCleared(placeholder: Drawable?) {
                 propicImageView.setImageDrawable(null)
             }
         }

         requestBuilder.into(target)
     }

     private fun saveBitmapToFile(bitmap: Bitmap): File {
         val file = File(requireContext().cacheDir, "propic.jpg")
         val outputStream = FileOutputStream(file)
         bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
         outputStream.flush()
         outputStream.close()
         return file
     }*/

    private fun uploadPropicAndUser(uid: String?) {
        val selectedImageBitmap = (propicImageView.drawable as BitmapDrawable).bitmap

        val byteArrayOutputStream = ByteArrayOutputStream()
        selectedImageBitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
        val imageData = byteArrayOutputStream.toByteArray()

        val storageReference = Firebase.storage.reference
        val propicReference = storageReference.child("propic/${uid}/profile.jpg")

        val uploadTask = propicReference.putBytes(imageData)
        uploadTask.addOnSuccessListener {

            propicReference.downloadUrl.addOnSuccessListener { uri ->
                val propicURL = uri.toString()
                addNewUserToFirestore(uid, propicURL)
            }
        }.addOnFailureListener {
            Toast.makeText(
                requireContext(),
                "Errore durante il caricamento dell'immagine, avvisa il nostro team di supporto (Errore: $it)",
                Toast.LENGTH_LONG
            ).show()
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
            "profileImage" to imageURL,
            "backdropImage" to Constants.DESERT_BACKDROP_URL,
            "movieMinutes" to 0,
            "moviesNumber" to 0,
            "tvMinutes" to 0,
            "tvNumber" to 0
        )
        val followDocument = hashMapOf(
            "followers" to arrayListOf<String>(),
            "following" to arrayListOf(),
            "people" to arrayListOf()
        )
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
            "movies" to hashMapOf<String, Map<String, Map<String, Any>>>(), "series" to hashMapOf()
        )

        FirestoreService.collectionUsers.document(uid!!).set(user).addOnFailureListener {
            Toast.makeText(
                requireContext(),
                "Registrazione al database fallita, avvisa il nostro team di supporto",
                Toast.LENGTH_LONG
            ).show()
        }
        FirestoreService.collectionFollow.document(uid).set(followDocument).addOnFailureListener {
            Toast.makeText(
                requireContext(),
                "Registrazione al database fallita, avvisa il nostro team di supporto",
                Toast.LENGTH_LONG
            ).show()
        }
        FirestoreService.collectionEpisodes.document(uid).set(episodesDocument)
            .addOnFailureListener {
                Toast.makeText(
                    requireContext(),
                    "Registrazione al database fallita, avvisa il nostro team di supporto",
                    Toast.LENGTH_LONG
                ).show()
            }
        FirestoreService.collectionUsersReviews.document(uid).set(reviewsDocument)
            .addOnFailureListener {
                Toast.makeText(
                    requireContext(),
                    "Registrazione al database fallita, avvisa il nostro team di supporto",
                    Toast.LENGTH_LONG
                ).show()
            }
        FirestoreService.collectionLists.document(uid).set(listsDocument).addOnSuccessListener {
            navigateToMainActivity()
        }.addOnFailureListener {
            Toast.makeText(
                requireContext(),
                "Registrazione al database fallita, avvisa il nostro team di supporto",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }
}