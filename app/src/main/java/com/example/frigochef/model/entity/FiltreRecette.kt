package com.example.frigochef.model.entity

import java.io.Serializable

data class FiltreRecette(
    val typeCuisine:  String?  = null,
    val typeRepas:    String?  = null,
    val difficulte:   String?  = null,
    val tempsMax:     Int?     = null,
    val isVege:       Boolean  = false,
    val isVegan:      Boolean  = false,
    val isSansGluten: Boolean  = false
) : Serializable