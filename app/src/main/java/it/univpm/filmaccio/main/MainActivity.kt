package it.univpm.filmaccio.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import it.univpm.filmaccio.R

// Questa activity è chiamata come se fosse l'activity principale entry point dell'applicazione.
// In realtà l'entry point è AuthActivity, che verifica se l'utente è loggato o meno e in base a questo
// reindirizza l'utente a questa activity o a LoginFragment.
// In questa activity abbiamo un NavHostFragment che si occupa di gestire i fragment che vengono mostrati
// all'interno di essa e una barra di navigazione in basso che permette di navigare tra i vari fragment.
class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var navBar: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView2) as NavHostFragment
        navController = navHostFragment.navController
        navBar = findViewById(R.id.navigation_bar)

        navBar.setupWithNavController(navController)
    }
}