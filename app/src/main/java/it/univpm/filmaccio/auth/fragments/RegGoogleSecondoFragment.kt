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
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import it.univpm.filmaccio.R
import it.univpm.filmaccio.databinding.FragmentRegGoogleSecondoBinding
import it.univpm.filmaccio.main.MainActivity
import it.univpm.filmaccio.main.utils.FirestoreService
import it.univpm.filmaccio.main.utils.UserUtils
import java.io.File
import java.io.FileOutputStream

// Questa classe gestisce il secondo passo della registrazione di un utente tramite Google.
// La differenza con il terzo passo della registrazione tramite email è che qui l'utente non viene creato in FirebaseAuth, perché in questo caso l'utente è già
// stato creato come ho spiegato in LoginFragment.
// Questa è la classe in cui ho il giga problema stranissimo di cui ho parlato abbondantemente in RegTerzaFragment, quindi nel caso andate a vedere quel file per la spiegazione (e vedrete anche che i due codici sono letteralmente identici).
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
    private lateinit var email: String
    private lateinit var username: String
    private lateinit var gender: String
    private lateinit var birthDate: Timestamp
    private lateinit var nameShown: String
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

    private fun onPropicClick() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    @Deprecated("Deprecated in Java")
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
    }

    private fun uploadPropicAndUser(uid: String?) {
        val storageRef = Firebase.storage.reference
        val propicRef = storageRef.child("propic/${uid}.jpg")
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
                val exception = task.exception
                Toast.makeText(
                    requireContext(),
                    "Errore durante il caricamento dell'immagine, avvisa il nostro team di supporto (Errore: ${exception.toString()})",
                    Toast.LENGTH_LONG
                )
                    .show()
            }

            propicRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                val imageURL = downloadUri.toString()
                addNewUserToFirestore(uid, imageURL)
            } else {
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
                Toast.makeText(
                    requireContext(),
                    "Registrazione al database fallita, avvisa il nostro team di supporto",
                    Toast.LENGTH_LONG
                ).show()
            }
        FirestoreService.collectionFollow.document(uid)
            .set(followDocument)
            .addOnFailureListener {
                Toast.makeText(
                    requireContext(),
                    "Registrazione al database fallita, avvisa il nostro team di supporto",
                    Toast.LENGTH_LONG
                ).show()
            }
        FirestoreService.collectionLists.document(uid)
            .set(listsDocument)
            .addOnSuccessListener {
                navigateToMainActivity()
            }
            .addOnFailureListener {
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