package com.example.frigochef

import com.example.frigochef.contract.QuestionnaireContract
import com.example.frigochef.model.entity.FiltreRecette
import com.example.frigochef.model.entity.Ingredient
import com.example.frigochef.model.repository.IngredientRepository
import com.example.frigochef.model.repository.SessionRepository
import com.example.frigochef.presenter.QuestionnairePresenter
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*


class QuestionnairePresenterTest {

    private lateinit var vue:                 QuestionnaireContract.View
    private lateinit var ingredientRepository: IngredientRepository
    private lateinit var sessionRepository:    SessionRepository
    private lateinit var presenter:            QuestionnairePresenter

    @Before
    fun setUp() {
        vue                  = mock()
        ingredientRepository = mock()
        sessionRepository    = mock()
        presenter            = QuestionnairePresenter(vue, ingredientRepository, sessionRepository)
    }

    @Test
    fun `chargerSessionPrecedente appelle afficherIngredientsPrecaches si session non vide`() {
        whenever(sessionRepository.findAllIds()).thenReturn(listOf(1L, 2L, 3L))

        presenter.chargerSessionPrecedente()

        verify(vue).afficherIngredientsPrecaches(listOf(1L, 2L, 3L))
    }

    @Test
    fun `rechercherIngredient appelle afficherIngredientsSuggeres avec les resultats`() {
        val ingredients = listOf(
            Ingredient(id = 1L, nom = "Ail",    categorie = "Légumes"),
            Ingredient(id = 2L, nom = "Aubergine", categorie = "Légumes")
        )
        whenever(ingredientRepository.findParNom("a")).thenReturn(ingredients)

        presenter.rechercherIngredient("a")

        verify(vue).afficherIngredientsSuggeres(ingredients)
    }

    @Test
    fun `valider sauvegarde les ingredients et navigue vers resultats`() {
        val filtres           = FiltreRecette(typeCuisine = "Italienne")
        val ingredientsCoches = listOf(1L, 2L, 3L)

        presenter.valider(filtres, ingredientsCoches)

        verify(sessionRepository).upsert(1L)
        verify(sessionRepository).upsert(2L)
        verify(sessionRepository).upsert(3L)
        verify(vue).naviguerVersResultats(filtres, ingredientsCoches)
    }
}