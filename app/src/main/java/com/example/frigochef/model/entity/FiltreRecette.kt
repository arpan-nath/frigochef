package com.example.frigochef.model.entity

import java.io.Serializable

/**
 * Regroupe les critères de filtrage sélectionnés par l'utilisateur.
 * Transmis de QuestionnaireActivity vers ResultatsActivity via Intent.
 * Les champs nullable indiquent qu'aucun filtre n'est appliqué pour ce critère.
 */

data class FiltreRecette(
    val typeCuisine:  String?  = null,
    val typeRepas:    String?  = null,
    val difficulte:   String?  = null,
    val tempsMax:     Int?     = null,
    val isVege:       Boolean  = false,
    val isVegan:      Boolean  = false,
    val isSansGluten: Boolean  = false
) : Serializable