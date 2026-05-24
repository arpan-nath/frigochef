package com.example.frigochef.model.entity

import java.io.Serializable


data class IngredientQuantite(
    val ingredientId: Long,
    val quantite:     Double,
    val unite:        String
) : Serializable