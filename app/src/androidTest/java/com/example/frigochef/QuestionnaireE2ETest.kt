package com.example.frigochef

// Code généré à l'aide de Claude AI
// Description : test bout-en-bout du parcours complet via le questionnaire —
//   sélection cuisine → contraintes → ingrédients → résultats → détail recette

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.espresso.matcher.ViewMatchers.Visibility
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.frigochef.model.repository.SessionRepository
import com.example.frigochef.view.QuestionnaireActivity
import org.hamcrest.Matchers.containsString
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuestionnaireE2ETest {

    private lateinit var sessionRepository: SessionRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<android.app.Application>()
        sessionRepository = SessionRepository(context)
        // Partir d'une session vide pour que chaque test soit indépendant
        sessionRepository.clearSession()
    }

    // ─────────────────────────────────────────────────────────────────────────
    // TEST 1 : Parcours complet — cuisine sélectionnée + ingrédients + résultats
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    fun parcours_complet_cuisine_ingredients_resultats_et_detail() {

        val context = ApplicationProvider.getApplicationContext<android.app.Application>()
        val intent  = Intent(context, QuestionnaireActivity::class.java)
        val scenario = ActivityScenario.launch<QuestionnaireActivity>(intent)

        // ── Étape 1 : Sélectionner une cuisine ──────────────────────────────
        // On clique sur la carte "Italienne" dans la grille
        onView(withText("Italienne"))
            .check(matches(isDisplayed()))
            .perform(click())

        // Continuer vers l'étape 2
        onView(withId(R.id.btnContinuer))
            .check(matches(isDisplayed()))
            .perform(click())

        // ── Étape 2 : Sélectionner le type de repas ─────────────────────────
        // On vérifie que les chips de contraintes sont visibles
        onView(withId(R.id.chipDiner))
            .check(matches(isDisplayed()))
            .perform(click())

        // Continuer vers l'étape 3
        onView(withId(R.id.btnContinuer))
            .check(matches(isDisplayed()))
            .perform(click())

        // ── Étape 3 : Saisir des ingrédients ────────────────────────────────
        // On vérifie que le champ de recherche est affiché
        onView(withId(R.id.etRechercheIngredient))
            .check(matches(isDisplayed()))

        // Saisir "Pâtes" dans la barre de recherche
        onView(withId(R.id.etRechercheIngredient))
            .perform(click(), typeText("Pâtes"), closeSoftKeyboard())

        // Attendre que la suggestion apparaisse et cliquer dessus
        onView(withId(R.id.rvSuggestionsRecherche))
            .check(matches(isDisplayed()))
        onView(withText(containsString("Pâtes")))
            .perform(click())

        // Vérifier que l'ingrédient a été ajouté à la liste "dans mon frigo"
        onView(withId(R.id.layoutIngredientsSaisis))
            .check(matches(isDisplayed()))

        // Saisir un deuxième ingrédient
        onView(withId(R.id.etRechercheIngredient))
            .perform(click(), typeText("Oeuf"), closeSoftKeyboard())

        onView(withId(R.id.rvSuggestionsRecherche))
            .check(matches(isDisplayed()))
        onView(withText(containsString("Oeuf")))
            .perform(click())

        // ── Cliquer sur "Voir les recettes" ─────────────────────────────────
        onView(withId(R.id.btnVoirRecettes))
            .check(matches(isDisplayed()))
            .perform(click())

        // ── Étape 4 : Vérifier l'écran des résultats ────────────────────────
        // Le RecyclerView de résultats doit être visible
        onView(withId(R.id.rvResultats))
            .check(matches(isDisplayed()))

        // Le message "X recettes correspondent" doit être affiché
        onView(withId(R.id.tvNombreResultats))
            .check(matches(isDisplayed()))

        // La zone "vide" ne doit pas être visible — on a des résultats
        onView(withId(R.id.layoutVide))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))

        // ── Étape 5 : Cliquer sur la première recette ────────────────────────
        onView(withId(R.id.rvResultats))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<
                        androidx.recyclerview.widget.RecyclerView.ViewHolder>(0, click())
            )

        // ── Étape 6 : Vérifier l'écran de détail ────────────────────────────
        onView(withId(R.id.tvNomRecette))
            .check(matches(isDisplayed()))
        onView(withId(R.id.rvIngredients))
            .check(matches(isDisplayed()))
        onView(withId(R.id.rvInstructions))
            .check(matches(isDisplayed()))

        scenario.close()
    }

    // ─────────────────────────────────────────────────────────────────────────
    // TEST 2 : Parcours "Peu importe" — aucun filtre → 30 recettes retournées
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    fun parcours_ignorer_toutes_etapes_retourne_resultats_non_vides() {

        val context  = ApplicationProvider.getApplicationContext<android.app.Application>()
        val intent   = Intent(context, QuestionnaireActivity::class.java)
        val scenario = ActivityScenario.launch<QuestionnaireActivity>(intent)

        // ── Étape 1 : Ignorer la sélection de cuisine ───────────────────────
        onView(withId(R.id.tvIgnorer))
            .check(matches(isDisplayed()))
            .perform(click())

        // ── Étape 2 : Ignorer les contraintes ───────────────────────────────
        onView(withId(R.id.tvIgnorer))
            .check(matches(isDisplayed()))
            .perform(click())

        // ── Étape 3 : Voir les recettes sans saisir d'ingrédient ─────────────
        onView(withId(R.id.btnVoirRecettes))
            .check(matches(isDisplayed()))
            .perform(click())

        // ── Vérifier que des résultats sont affichés ─────────────────────────
        // Sans filtre, toutes les recettes du catalogue doivent apparaître
        onView(withId(R.id.rvResultats))
            .check(matches(isDisplayed()))

        onView(withId(R.id.layoutVide))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))

        // Le compteur doit mentionner plusieurs recettes
        onView(withId(R.id.tvNombreResultats))
            .check(matches(isDisplayed()))

        scenario.close()
    }

    // ─────────────────────────────────────────────────────────────────────────
    // TEST 3 : Parcours avec plusieurs cuisines → résultats fusionnés
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    fun parcours_multi_cuisine_affiche_resultats_des_deux_cuisines() {

        val context  = ApplicationProvider.getApplicationContext<android.app.Application>()
        val intent   = Intent(context, QuestionnaireActivity::class.java)
        val scenario = ActivityScenario.launch<QuestionnaireActivity>(intent)

        // ── Étape 1 : Sélectionner deux cuisines ────────────────────────────
        onView(withText("Grecque"))
            .check(matches(isDisplayed()))
            .perform(click())

        onView(withText("Mexicaine"))
            .check(matches(isDisplayed()))
            .perform(click())

        onView(withId(R.id.btnContinuer)).perform(click())

        // ── Étape 2 : Ignorer les contraintes ───────────────────────────────
        onView(withId(R.id.tvIgnorer)).perform(click())

        // ── Étape 3 : Voir les recettes sans ingrédient ──────────────────────
        onView(withId(R.id.btnVoirRecettes)).perform(click())

        // ── Vérifier que les résultats des deux cuisines sont présents ────────
        // Grecque a 3 recettes, Mexicaine a 3 recettes → au moins 6 résultats
        onView(withId(R.id.rvResultats))
            .check(matches(isDisplayed()))

        onView(withId(R.id.layoutVide))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))

        // On vérifie qu'une recette grecque et une mexicaine sont bien là
        onView(withText(containsString("Salade grecque")))
            .check(matches(isDisplayed()))

        onView(withText(containsString("Guacamole")))
            .check(matches(isDisplayed()))

        scenario.close()
    }
}