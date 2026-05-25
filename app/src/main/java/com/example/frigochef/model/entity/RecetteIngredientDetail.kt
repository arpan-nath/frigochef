package com.example.frigochef.model.entity

data class RecetteIngredientDetail(
    val ingredientId: Long,
    val nom:          String,
    val quantite:     String,
    val uniteMesure:  String
)