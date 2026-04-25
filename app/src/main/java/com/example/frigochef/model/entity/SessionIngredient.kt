package com.example.frigochef.model.entity

data class SessionIngredient(
    val id: Long = 0,
    val ingredientId: Long,
    val dateDerniereSaisie: Long,
    val frequenceUsage: Int = 1
)
