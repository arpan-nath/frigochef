package com.example.frigochef

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.frigochef.view.AccueilActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AccueilSearchE2ETest {

    @get:Rule
    val activityRule = ActivityScenarioRule(AccueilActivity::class.java)

    @Test
    fun parcours_complet_recherche_recette_et_affichage_detail() {

        // ── 1. Vérifier que l'écran d'accueil est affiché ──
        onView(withId(R.id.rvRecettes))
            .check(matches(isDisplayed()))

        // ── 2. Saisir un nom de recette dans la barre de recherche ──
        onView(withId(R.id.etRecherche))
            .perform(click(), typeText("Houmous"), closeSoftKeyboard())

        // ── 3. Vérifier que la liste se filtre en temps réel ──
        onView(withId(R.id.rvRecettes))
            .check(matches(isDisplayed()))
        onView(withId(R.id.layoutVide))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))

        // ── 4. Cliquer sur la première recette ──
        onView(withId(R.id.rvRecettes))
            .perform(RecyclerViewActions.actionOnItemAtPosition<androidx.recyclerview.widget.RecyclerView.ViewHolder>(0, click()))

        // ── 5. Vérifier l'affichage du détail ──
        onView(withId(R.id.tvNomRecette))
            .check(matches(isDisplayed()))
        onView(withId(R.id.rvIngredients))
            .check(matches(isDisplayed()))
        onView(withId(R.id.rvInstructions))
            .check(matches(isDisplayed()))
    }
}