package it.univpm.filmaccio.main.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import it.univpm.filmaccio.R

/**
 * Activity principale dell'applicazione.
 * Contiene un NavHostFragment che si occupa di gestire i fragment che vengono mostrati
 * all'interno di essa e una barra di navigazione in basso che permette di navigare tra i vari fragment.
 *
 * @author nicolobartolinii
 */
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