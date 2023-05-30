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

        navController.addOnDestinationChangedListener { _, destination, _ ->
            navBar.menu.findItem(destination.id)?.isChecked = true
        }

        navBar.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    // Carica il fragment HomeFragment
                    loadFragment(HomeFragment())
                    true
                }
                R.id.navigation_feed -> {
                    // Carica il fragment FeedFragment
                    loadFragment(FeedFragment())
                    true
                }
                R.id.navigation_search -> {
                    // Carica il fragment EpisodesFragment
                    loadFragment(SearchFragment())
                    true
                }
                R.id.navigation_episodes -> {
                    // Carica il fragment EpisodesFragment
                    loadFragment(EpisodesFragment())
                    true
                }
                R.id.navigation_profile -> {
                    // Carica il fragment EpisodesFragment
                    loadFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView2, fragment)
            .commit()
    }
}