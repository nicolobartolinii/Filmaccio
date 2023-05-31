package it.univpm.filmaccio.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import it.univpm.filmaccio.R
import it.univpm.filmaccio.main.fragments.EpisodesFragment
import it.univpm.filmaccio.main.fragments.FeedFragment
import it.univpm.filmaccio.main.fragments.HomeFragment
import it.univpm.filmaccio.main.fragments.ProfileFragment
import it.univpm.filmaccio.main.fragments.SearchFragment

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var navBar: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView2) as NavHostFragment
        navController = navHostFragment.navController
        navBar = findViewById(R.id.navigation_bar)

        navBar.setupWithNavController(navController)
    }
}