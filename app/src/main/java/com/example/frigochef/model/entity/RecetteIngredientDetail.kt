package com.example.frigochef.model.entity

/**
 * Vue dénormalisée d'un ingrédient requis par une recette.
 * Combine les données de ingredient et recette_ingredient via jointure SQL.
 * Utilisé par IngredientDetailAdapter pour afficher le statut possédé/manquant.
 */

data class RecetteIngredientDetail(
    val ingredientId: Long,
    val nom:          String,
    val quantite:     String,
    val uniteMesure:  String
)