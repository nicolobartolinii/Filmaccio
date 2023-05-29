package it.univpm.filmaccio.auth.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.storage.FirebaseStorage
import it.univpm.filmaccio.R
import it.univpm.filmaccio.main.MainActivity
import java.io.File
import java.io.FileOutputStream

@Suppress("DEPRECATION")
class RegTerzaFragment : Fragment() {

    companion object {
        private const val PICK_IMAGE_REQUEST = 7
    }

    private lateinit var buttonBack: Button
    private lateinit var buttonFine: Button
    private lateinit var nomeVisualizzatoTextInputEditText: TextInputEditText
    private lateinit var nomeVisualizzatoTectInputLayout: TextInputLayout
    private lateinit var propicImageView: ImageView
    private lateinit var selectedImageUri: Uri
    private lateinit var email: String
    private lateinit var username: String
    private lateinit var password: String
    private lateinit var gender: String
    private lateinit var birthDate: Timestamp
    private lateinit var nameShown: String
    private var croppedImageFile: File? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_reg_terza, container, false)

        buttonBack = view.findViewById(R.id.buttonBack)
        buttonFine = view.findViewById(R.id.buttonFine)
        nomeVisualizzatoTextInputEditText =
            view.findViewById(R.id.nomeVisualizzatoTextInputEditText)
        nomeVisualizzatoTectInputLayout = view.findViewById(R.id.nomeVisualizatoTextInputLayout)
        propicImageView = view.findViewById(R.id.propicSetImageView)

        val args: RegTerzaFragmentArgs by navArgs()
        email = args.email
        username = args.username
        password = args.password
        gender = args.gender
        birthDate = args.birthDate

        nomeVisualizzatoTextInputEditText.setText(username, TextView.BufferType.EDITABLE)

        buttonBack.setOnClickListener {
            Navigation.findNavController(view)
                .navigate(R.id.action_regTerzaFragment_to_regSecondaFragment)
        }

        buttonFine.setOnClickListener {
            nomeVisualizzatoTectInputLayout.isErrorEnabled = false
            nameShown = nomeVisualizzatoTextInputEditText.text.toString()

            if (nameShown.length > 50 || nameShown.isEmpty() || nameShown.length < 3) {
                nomeVisualizzatoTectInputLayout.isErrorEnabled = true
                nomeVisualizzatoTectInputLayout.error = "Il nome visualizzato deve essere lungo tra 3 e 50 caratteri"
                return@setOnClickListener
            }

            val auth = FirebaseAuth.getInstance()
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        val uid = user?.uid
                        user?.sendEmailVerification()
                            ?.addOnCompleteListener { verifyTask ->
                                if (verifyTask.isSuccessful) {
                                    MaterialAlertDialogBuilder(requireContext())
                                        .setTitle("Verifica la tua email")
                                        .setMessage("Benvenuto in Filmaccio! Per sbloccare tutte le funzionalità dell'app, ricordati di verificare il tuo indirizzo email cliccando sul link che ti abbiamo inviato.")
                                        .setPositiveButton("OK") { dialog, _ ->
                                            dialog.dismiss()
                                        }
                                        .show()
                                } else {
                                    Toast.makeText(
                                        requireContext(),
                                        "Invio dell'email di verifica fallito, contatta il nostro team di supporto",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        uploadPropicAndUser(uid)
                    } else {
                        Toast.makeText(requireContext(), "Registrazione fallita, riprova", Toast.LENGTH_LONG)
                            .show()
                        Navigation.findNavController(view)
                            .navigate(R.id.action_regTerzaFragment_to_loginFragment)
                    }
                }
        }

        propicImageView.setOnClickListener { onPropicClick() }

        return view
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
        val file = File(requireContext().cacheDir, "propic.jpg")
        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.flush()
        outputStream.close()
        return file
    }

    private fun uploadPropicAndUser(uid: String?) {
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference
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
                // Si è verificato un errore durante il caricamento dell'immagine
                val exception = task.exception
                Toast.makeText(requireContext(), "Errore durante il caricamento dell'immagine, avvisa il nostro team di supporto (Errore: ${exception.toString()})", Toast.LENGTH_LONG)
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
                Toast.makeText(requireContext(), "Errore durante il caricamento dell'immagine, avvisa il nostro team di supporto (Errore: ${exception.toString()})", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun addNewUserToFirestore(uid: String?, imageURL: String) {
        val firestore = FirebaseFirestore.getInstance()
        val usersCollection = firestore.collection("users")

        val user = hashMapOf(
            "uid" to uid,
            "email" to email,
            "username" to username,
            "gender" to gender,
            "birthDate" to birthDate,
            "nameShown" to nameShown,
            "profileImage" to imageURL
        )

        usersCollection.document(uid!!)
            .set(user)
            .addOnSuccessListener {
                // Utente aggiunto con successo al database Firestore
                navigateToHomeActivity()
            }
            .addOnFailureListener {
                // Si è verificato un errore durante l'aggiunta dell'utente al database Firestore
                Toast.makeText(requireContext(), "Registrazione al database fallita, avvisa il nostro team di supporto", Toast.LENGTH_LONG).show()
            }
    }

    private fun navigateToHomeActivity() {
        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }
}