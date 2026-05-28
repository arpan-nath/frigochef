package com.example.frigochef.model.entity

/**
 * Représente un ingrédient du catalogue de référence.
 * Données pré-chargées en lecture seule dans la table ingredient.
 */
data class Ingredient(
    val id: Long = 0,
    val nom: String,
    val categorie: String
)
