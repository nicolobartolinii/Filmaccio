package it.univpm.filmaccio

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.Navigation

class LoginFragment : Fragment() {

    private lateinit var regEmailButton: Button
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        regEmailButton = view.findViewById<Button>(R.id.buttonRegEmail)

        regEmailButton.setOnClickListener { Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_regPrimaFragment)}

        return view
    }
}