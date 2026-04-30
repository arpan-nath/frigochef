package com.example.frigochef.view.contract

import com.example.frigochef.model.entity.Recette
import com.example.frigochef.model.entity.RecetteAvecScore

interface ResultatsContract {

    interface View {
        fun afficherResultats(recettes: List<RecetteAvecScore>)
        fun afficherEtatVide()
        fun afficherNombreResultats(count: Int)
    }

    interface Presenter {
        fun chargerResultats(
            typeCuisine:       String?,
            typeRepas:         String?,
            difficulte:        String?,
            tempsMax:          Int?,
            isVege:            Boolean,
            isVegan:           Boolean,
            isSansGluten:      Boolean,
            ingredientsDispos: List<Long>
        )
    }
}