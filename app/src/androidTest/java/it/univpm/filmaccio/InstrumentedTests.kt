package it.univpm.filmaccio

import android.widget.EditText
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.hasChildCount
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import it.univpm.filmaccio.details.activities.MovieDetailsActivity
import org.hamcrest.Matchers.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Questa classe contiene i test strumentali dell'applicazione.
 * I test strumentali sono test che vengono eseguiti direttamente sul dispositivo.
 *
 * @author nicolobartolinii
 */
@RunWith(AndroidJUnit4::class)
class InstrumentedTests {

    @Before
    fun setUp() {
        Intents.init()
    }

    @After
    fun cleanUp() {
        Intents.release()
    }

    @Test
    fun clickPosterNavigatesToMovieDetailsActivity() {
        // Questo tempo di attesa è necessario per permettere al fragment di caricare i dati
        Thread.sleep(10000)
        // Clicca sul primo poster (rappresentante uno dei film attualmente al cinema)
        onView(withId(R.id.first_latest_release_home)).perform(click())
        // Controlla se l'activity di dettaglio del film è stata avviata
        intended(hasComponent(MovieDetailsActivity::class.java.name))
    }

    @Test
    fun testNavigationToProfileFragment() {
        // Simula un click sull'icona del profilo nella barra di navigazione
        onView(withId(R.id.navigation_profile)).perform(click())
        // Verifica che il fragment del profilo sia visualizzato
        onView(withId(R.id.navigation_profile)).check(matches(isDisplayed()))
    }

    @Test
    fun testSearchFunctionality() {
        // Naviga al fragment di ricerca
        onView(withId(R.id.navigation_search)).perform(click())
        // Verifica che il fragment di ricerca sia visualizzato correttamente
        onView(withId(R.id.view_flipper_search)).check(
            matches(
                hasDescendant(
                    withEffectiveVisibility(
                        ViewMatchers.Visibility.VISIBLE
                    )
                )
            )
        )
        // Simula un click sulla barra di ricerca
        onView(withId(R.id.search_bar)).perform(click())
        // Scrive la query di ricerca nella barra di ricerca
        val query = "better call saul"
        onView(isAssignableFrom(EditText::class.java)).perform(typeText(query))
        // Questo tempo è necessario per permettere all'app di effettuare la ricerca
        Thread.sleep(1500)
        // Controlla che la recycler view dei risultati di ricerca sia stata popolata
        onView(withId(R.id.search_recycler_view)).check(matches(not(hasChildCount(0))))
    }


}