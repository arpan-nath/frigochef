package com.example.frigochef.model.entity

data class RecetteIngredient(
    val id: Long = 0,
    val recetteId: Long,
    val ingredientId: Long,
    val quantite: String?,
    val uniteMesure:  String?
)
