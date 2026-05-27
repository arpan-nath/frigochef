package com.example.frigochef

import com.example.frigochef.contract.AccueilContract
import com.example.frigochef.model.entity.FiltreRecette
import com.example.frigochef.model.entity.Ingredient
import com.example.frigochef.model.entity.Recette
import com.example.frigochef.model.repository.IngredientRepository
import com.example.frigochef.model.repository.RecetteRepository
import com.example.frigochef.model.repository.SessionRepository
import com.example.frigochef.presenter.AccueilPresenter
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*


class AccueilPresenterTest {

    private lateinit var vue:                  AccueilContract.View
    private lateinit var repository:           RecetteRepository
    private lateinit var sessionRepository:    SessionRepository
    private lateinit var ingredientRepository: IngredientRepository
    private lateinit var presenter: AccueilPresenter

    private val recetteFake = Recette(
        id = 1L, nom = "Houmous", description = null,
        instructions = "", tempsPrep = 15,
        difficulte = "Facile", typeCuisine = "Moyen-Orientale",
        typeRepas = "Collation", imageUrl = null, portions = 6
    )

    @Before
    fun setUp() {
        vue                  = mock()
        repository           = mock()
        sessionRepository    = mock()
        ingredientRepository = mock()
        presenter = AccueilPresenter(vue, repository, sessionRepository, ingredientRepository)
    }

    @Test
    fun `chargerRecettes appelle afficherRecettes si liste non vide`() {
        whenever(repository.findAll()).thenReturn(listOf(recetteFake))
        presenter.chargerRecettes()
        verify(vue).afficherRecettes(listOf(recetteFake))
    }

    @Test
    fun `chargerRecettes appelle afficherMessageVide si liste vide`() {
        whenever(repository.findAll()).thenReturn(emptyList())
        presenter.chargerRecettes()
        verify(vue).afficherMessageVide()
    }

    @Test
    fun `rechercherRecettes appelle afficherRecettes si resultats`() {
        whenever(repository.findParNom("Houmous")).thenReturn(listOf(recetteFake))
        presenter.rechercherRecettes("Houmous")
        verify(vue).afficherRecettes(listOf(recetteFake))
    }

    @Test
    fun `rechercherRecettes appelle afficherMessageVide si aucun resultat`() {
        whenever(repository.findParNom("xyz")).thenReturn(emptyList())
        presenter.rechercherRecettes("xyz")
        verify(vue).afficherMessageVide()
    }

    @Test
    fun `filtrerParFiltres appelle afficherRecettes si resultats`() {
        val filtres = FiltreRecette(difficulte = "Facile")
        whenever(repository.findParFiltres(filtres)).thenReturn(listOf(recetteFake))
        presenter.filtrerParFiltres(filtres)
        verify(vue).afficherRecettes(listOf(recetteFake))
    }

    @Test
    fun `filtrerParFiltres appelle afficherMessageVide si aucun resultat`() {
        val filtres = FiltreRecette(difficulte = "Difficile")
        whenever(repository.findParFiltres(filtres)).thenReturn(emptyList())
        presenter.filtrerParFiltres(filtres)
        verify(vue).afficherMessageVide()
    }

    @Test
    fun `chargerSessionIngredients appelle afficherChipsSession`() {
        whenever(sessionRepository.findAllIds()).thenReturn(listOf(1L, 2L))
        whenever(ingredientRepository.findById(1L)).thenReturn(
            Ingredient(1L, "Ail", "Légumes")
        )
        whenever(ingredientRepository.findById(2L)).thenReturn(
            Ingredient(2L, "Oignon", "Légumes")
        )
        presenter.chargerSessionIngredients()
        verify(vue).afficherChipsSession(listOf("Ail", "Oignon"))
    }
}