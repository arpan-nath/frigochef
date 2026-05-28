package com.example.frigochef.model.entity

/**
 * Associe une recette à son score de compatibilité calculé.
 * Produit par ResultatsPresenter et transmis à ResultatsActivity
 * pour l'affichage trié par score décroissant.
 */
data class RecetteAvecScore(
    val recette: Recette,
    val score:   Int
)
