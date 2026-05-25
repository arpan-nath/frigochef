package com.example.frigochef

import com.example.frigochef.contract.DetailContract
import com.example.frigochef.model.entity.IngredientQuantite
import com.example.frigochef.model.entity.Recette
import com.example.frigochef.model.entity.RecetteIngredientDetail
import com.example.frigochef.model.repository.RecetteRepository
import com.example.frigochef.presenter.DetailPresenter
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

class DetailPresenterTest {

    private lateinit var vue:        DetailContract.View
    private lateinit var repository: RecetteRepository
    private lateinit var presenter:  DetailPresenter

    @Before
    fun setUp() {
        vue        = mock()
        repository = mock()
        presenter  = DetailPresenter(vue, repository)
    }

    @Test
    fun `afficherErreur si recette introuvable`() {
        whenever(repository.findById(99L)).thenReturn(null)

        presenter.chargerDetail(99L, emptyList())

        verify(vue).afficherErreur(any())
        verify(vue, never()).afficherRecette(any())
    }

    @Test
    fun `afficherRecette si recette trouvee`() {
        val recette = Recette(
            id = 1L, nom = "Houmous", description = null,
            instructions = "", tempsPrep = 15,
            difficulte = "Facile", typeCuisine = "Moyen-Orientale",
            typeRepas = "Collation", imageUrl = null, portions = 6
        )
        whenever(repository.findById(1L)).thenReturn(recette)
        whenever(repository.findIngredientsDetailParRecette(1L)).thenReturn(emptyList())

        presenter.chargerDetail(1L, emptyList())

        verify(vue).afficherRecette(recette)
    }

    @Test
    fun `afficherIngredients appele avec bons ingredients`() {
        val recette = Recette(
            id = 1L, nom = "Houmous", description = null,
            instructions = "", tempsPrep = 15,
            difficulte = "Facile", typeCuisine = "Moyen-Orientale",
            typeRepas = "Collation", imageUrl = null, portions = 6
        )
        val ingredients = listOf(
            RecetteIngredientDetail(14L, "Pois chiches", "400", "g"),
            RecetteIngredientDetail(1L,  "Ail",          "2",   "gousse")
        )
        val dispos = listOf(IngredientQuantite(14L, 400.0, "g"))

        whenever(repository.findById(1L)).thenReturn(recette)
        whenever(repository.findIngredientsDetailParRecette(1L)).thenReturn(ingredients)

        presenter.chargerDetail(1L, dispos)

        verify(vue).afficherIngredients(ingredients, dispos)
    }
}