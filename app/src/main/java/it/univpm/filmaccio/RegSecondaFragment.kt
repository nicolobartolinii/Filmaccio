package it.univpm.filmaccio

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.Navigation

class RegSecondaFragment : Fragment() {

    private lateinit var buttonBack: Button
    private lateinit var buttonAvanti: Button
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_reg_seconda, container, false)

        buttonBack = view.findViewById<Button>(R.id.buttonBack)
        buttonAvanti = view.findViewById<Button>(R.id.buttonAvanti)

        buttonBack.setOnClickListener { Navigation.findNavController(view).navigate(R.id.action_regSecondaFragment_to_regPrimaFragment)}
        buttonAvanti.setOnClickListener { Navigation.findNavController(view).navigate(R.id.action_regPrimaFragment_to_regSecondaFragment) }

        return view
    }
}