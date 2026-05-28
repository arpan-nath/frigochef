package com.example.frigochef.model.entity

import java.io.Serializable

/**
 * Associe un ingrédient à une quantité et une unité de mesure.
 * Utilisé pour représenter les ingrédients saisis par l'utilisateur
 * dans le Questionnaire, et pour calculer le score de compatibilité.
 */

data class IngredientQuantite(
    val ingredientId: Long,
    val quantite:     Double,
    val unite:        String
) : Serializable