package com.example.frigochef.model.entity

/**
 * Représente une ligne de la table de jointure recette_ingredient.
 * Contient la quantité et l'unité requises pour un ingrédient dans une recette.
 */

data class RecetteIngredient(
    val id: Long = 0,
    val recetteId: Long,
    val ingredientId: Long,
    val quantite: String?,
    val uniteMesure:  String?
)
