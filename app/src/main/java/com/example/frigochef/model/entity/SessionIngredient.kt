package com.example.frigochef.model.entity

/**
 * Représente un ingrédient sauvegardé dans la session précédente.
 * La fréquence d'usage permet de trier les ingrédients par popularité.
 * La date de dernière saisie est stockée en millisecondes (Unix timestamp).
 */

data class SessionIngredient(
    val id: Long = 0,
    val ingredientId: Long,
    val dateDerniereSaisie: Long,
    val frequenceUsage: Int = 1
)
