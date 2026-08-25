package com.example.frigochef

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.frigochef.model.repository.SessionRepository
import com.example.frigochef.view.QuestionnaireActivity
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Test de bout-en-bout vérifiant la persistance de la session utilisateur.
 *
 * Ce test simule le comportement de l'application lorsque l'utilisateur
 * navigue dans l'application ou relance une activité. Il permet de valider
 * que les informations importantes de session sont conservées correctement
 * et que l'application redirige l'utilisateur vers le bon écran.
 */

@RunWith(AndroidJUnit4::class)
class SessionPersistanceTest {

    private lateinit var sessionRepository: SessionRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<android.app.Application>()
        sessionRepository = SessionRepository(context)

        // Vider la session avant chaque test pour partir d'un état propre
        sessionRepository.clearSession()
    }

    @Test
    fun ingredientsSauvegardesEtPrecochesAuRelancement() {

        val context = ApplicationProvider.getApplicationContext<android.app.Application>()

        // ── Étape 1 : Simuler une session précédente ──────────────────────
        // On insère directement les ingrédients comme si l'utilisateur
        // les avait saisis lors d'une session précédente
        sessionRepository.upsert(1L)  // Ail
        sessionRepository.upsert(2L)  // Oignon
        sessionRepository.upsert(17L) // Poulet

        // ── Étape 2 : Relancer le Questionnaire ───────────────────────────
        // Simule un utilisateur qui relance l'application après l'avoir quittée
        val intent = Intent(context, QuestionnaireActivity::class.java)
        val scenario = ActivityScenario.launch<QuestionnaireActivity>(intent)

        // ── Étape 3 : Naviguer jusqu'à l'étape 3 (Ingrédients) ───────────
        // Étape 1 → 2
        onView(withId(R.id.btnContinuer)).perform(click())
        // Étape 2 → 3
        onView(withId(R.id.btnContinuer)).perform(click())

        // ── Étape 4 : Vérifier que les chips de session sont affichés ─────
        // La section "Session précédente" doit être visible
        onView(withId(R.id.layoutSessionPrecedente))
            .check(matches(isDisplayed()))

        // Les chips des ingrédients de la session précédente doivent être visibles
        onView(withText(org.hamcrest.Matchers.containsString("Ail")))
            .check(matches(isDisplayed()))

        onView(withText(org.hamcrest.Matchers.containsString("Oignon")))
            .check(matches(isDisplayed()))

        onView(withText(org.hamcrest.Matchers.containsString("Poulet")))
            .check(matches(isDisplayed()))

        scenario.close()
    }
}