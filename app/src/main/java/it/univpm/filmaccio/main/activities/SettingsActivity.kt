package it.univpm.filmaccio.main.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.firebase.auth.FirebaseAuth
import it.univpm.filmaccio.R
import it.univpm.filmaccio.auth.AuthActivity

/**
 * Questa classe è l'activity che gestisce le impostazioni dell'applicazione.
 *
 * @author NicolaPiccia
 */
class SettingsActivity : AppCompatActivity() {

    // creo la activity e imposta il layout sul mio xml
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings) // R da indicazione cartella res di resource
        if (savedInstanceState == null) { // se è la prima volta che viene attivata
            supportFragmentManager.beginTransaction().replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment : PreferenceFragmentCompat(),
        SharedPreferences.OnSharedPreferenceChangeListener {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences, rootKey)

            val colorStateList =
                ContextCompat.getColorStateList(requireContext(), R.color.icon_color)
            findPreference<Preference>("theme")?.icon?.setTintList(colorStateList)
            findPreference<Preference>("logout")?.icon?.setTintList(colorStateList)

            val logoutPref: Preference? = findPreference("logout")
            logoutPref?.setOnPreferenceClickListener {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(requireContext(), AuthActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
                true
            }
        }

        override fun onResume() {
            super.onResume()
            preferenceScreen.sharedPreferences?.registerOnSharedPreferenceChangeListener(this)
        }

        override fun onPause() {
            super.onPause()
            preferenceScreen.sharedPreferences?.unregisterOnSharedPreferenceChangeListener(this)
        }

        override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
            if (key == "theme") {
                when (sharedPreferences.getString("theme", "default")) {
                    "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    "system" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                }
            }
        }
    }
}