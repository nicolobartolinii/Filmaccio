package it.univpm.filmaccio

import android.app.Activity
import android.content.Intent
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
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.bumptech.glide.Glide

class RegTerzaFragment : Fragment() {

    private lateinit var buttonBack: Button
    private lateinit var buttonFine: Button
    private lateinit var nomeVisualizzatoTextInputEditText: TextInputEditText
    private lateinit var nomeVisualizzatoTectInputLayout: TextInputLayout
    private lateinit var propicImageView: ImageView
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_reg_terza, container, false)

        buttonBack = view.findViewById<Button>(R.id.buttonBack)
        buttonFine = view.findViewById<Button>(R.id.buttonFine)
        nomeVisualizzatoTextInputEditText = view.findViewById(R.id.nomeVisualizzatoTextInputEditText)
        nomeVisualizzatoTectInputLayout = view.findViewById(R.id.nomeVisualizatoTextInputLayout)
        propicImageView = view.findViewById(R.id.propicSetImageView)

        val args: RegTerzaFragmentArgs by navArgs()
        val email: String = args.email
        val username: String = args.username
        val password: String = args.password
        val gender: String = args.gender
        val birthDate: Timestamp = args.birthDate

        nomeVisualizzatoTextInputEditText.setText(username, TextView.BufferType.EDITABLE)

        buttonBack.setOnClickListener { Navigation.findNavController(view).navigate(R.id.action_regTerzaFragment_to_regSecondaFragment)}

        buttonFine.setOnClickListener {
            val nameShown = nomeVisualizzatoTextInputEditText.text.toString()
            val propic = propicImageView.drawable
        }

        return view
    }

    private val PICK_IMAGE_REQUEST = 7
    private fun onPropicClick(view: View) {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri: Uri? = data.data
            // ... Continua con il codice per il caricamento dell'immagine ...
            loadImageWithCircularCrop(selectedImageUri)
        }
    }

    private fun loadImageWithCircularCrop(imageUri: Uri?) {
        Glide.with(this)
            .load(imageUri)
            .circleCrop()
            .into(propicImageView)
    }

}