package com.example.frigochef.model

object ScoreCalculateur {

    /**
     * Calcule le score de compatibilité d'une recette.
     * Système binaire — possède ou ne possède pas.
     *
     * @param ingredientsRecette  IDs requis par la recette
     * @param ingredientsDispos   IDs disponibles chez l'utilisateur
     * @return Score entre 0 et 100
     */
    fun calculer(
        ingredientsRecette: List<Long>,
        ingredientsDispos:  List<Long>
    ): Int {
        if (ingredientsRecette.isEmpty()) return 0
        val possedes = ingredientsRecette.count { it in ingredientsDispos }
        return ((possedes.toFloat() / ingredientsRecette.size) * 100).toInt()
    }

    fun couleurScore(score: Int): Int = when {
        score >= 75 -> android.R.color.holo_green_dark
        score >= 50 -> android.R.color.holo_orange_dark
        else        -> android.R.color.holo_red_dark
    }
}