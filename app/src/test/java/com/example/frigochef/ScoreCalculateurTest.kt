package com.example.frigochef

import com.example.frigochef.model.ScoreCalculateur
import com.example.frigochef.model.entity.IngredientQuantite
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Test

class ScoreCalculateurTest {

    @Test
    fun `score 100 si tous les ingredients presents`() {
        val score = ScoreCalculateur.calculer(
            listOf(
                IngredientQuantite(1L, 1.0, "unité"),
                IngredientQuantite(2L, 1.0, "unité"),
                IngredientQuantite(3L, 1.0, "unité")
            ),
            listOf(
                IngredientQuantite(1L, 1.0, "unité"),
                IngredientQuantite(2L, 1.0, "unité"),
                IngredientQuantite(3L, 1.0, "unité")
            )
        )
        assertEquals(100, score)
    }

    @Test
    fun `score 0 si aucun ingredient dispo`() {
        val score = ScoreCalculateur.calculer(
            listOf(
                IngredientQuantite(1L, 1.0, "unité"),
                IngredientQuantite(2L, 1.0, "unité"),
                IngredientQuantite(3L, 1.0, "unité")
            ),
            emptyList()
        )
        assertEquals(0, score)
    }

    @Test
    fun `score proportionnel si quantite insuffisante`() {
        val score = ScoreCalculateur.calculer(
            listOf(IngredientQuantite(1L, 600.0, "g")),
            listOf(IngredientQuantite(1L, 300.0, "g"))
        )
        assertEquals(50, score)
    }
}