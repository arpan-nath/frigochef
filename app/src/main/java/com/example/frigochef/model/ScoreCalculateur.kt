package com.example.frigochef.model

import com.example.frigochef.model.entity.IngredientQuantite

object ScoreCalculateur {

    /**
     * Calcule le score de compatibilité d'une recette.
     *
     * @param ingredientsRecette  Les ingrédients requis par la recette (avec quantités)
     * @param ingredientsDispos   Les ingrédients que l'utilisateur possède (avec quantités)
     * @return Score entre 0 et 100
     */
    fun calculer(
        ingredientsRecette: List<IngredientQuantite>,
        ingredientsDispos:  List<IngredientQuantite>
    ): Int {
        if (ingredientsRecette.isEmpty()) return 0

        var totalScore = 0.0

        ingredientsRecette.forEach { requis ->

            // Cherche si l'utilisateur a cet ingrédient
            val dispo = ingredientsDispos.find { it.ingredientId == requis.ingredientId }

            val proportion = if (dispo == null) {
                // L'utilisateur n'a pas cet ingrédient
                0.0
            } else {
                // Calcule la proportion — plafonnée à 1.0
                // Ex: 300g dispo / 600g requis = 0.5
                // Ex: 500ml dispo / 200ml requis = min(2.5, 1.0) = 1.0
                (dispo.quantite / requis.quantite).coerceAtMost(1.0)
            }

            totalScore += proportion
        }

        // Moyenne des proportions × 100
        return ((totalScore / ingredientsRecette.size) * 100).toInt()
    }

    /**
     * Retourne la couleur selon le score.
     * Vert ≥ 75%, Orange ≥ 50%, Rouge < 50%
     */
    fun couleurScore(score: Int): Int = when {
        score >= 75 -> android.R.color.holo_green_dark
        score >= 50 -> android.R.color.holo_orange_dark
        else        -> android.R.color.holo_red_dark
    }
}