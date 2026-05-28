package com.example.frigochef.model.entity

/**
 * Représente une recette du catalogue.
 * Données pré-chargées en lecture seule dans la table recette.
 * Les booléens isVege, isVegan et isSansGluten permettent le filtrage
 * par régime alimentaire.
 */

data class Recette(
    val id:           Long = 0,
    val nom:          String,
    val description:  String?,
    val instructions: String,
    val tempsPrep:    Int,
    val difficulte:   String,
    val typeCuisine:  String,
    val typeRepas:    String,
    val imageUrl:     String?,
    val isVege:       Boolean = false,
    val isVegan:      Boolean = false,
    val isSansGluten: Boolean = false,
    val portions:     Int = 4
)