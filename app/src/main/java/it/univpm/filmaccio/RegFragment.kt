package it.univpm.filmaccio

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.Navigation

class RegFragment : Fragment() {

    private lateinit var buttonBack: Button
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_reg, container, false)

        buttonBack = view.findViewById<Button>(R.id.buttonBack)

        buttonBack.setOnClickListener { Navigation.findNavController(view).navigate(R.id.action_regFragment_to_loginFragment)}

        return view
    }
}