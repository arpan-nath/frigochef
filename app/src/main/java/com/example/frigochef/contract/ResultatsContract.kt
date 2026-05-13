package com.example.frigochef.contract

import com.example.frigochef.model.entity.FiltreRecette
import com.example.frigochef.model.entity.IngredientQuantite
import com.example.frigochef.model.entity.Recette
import com.example.frigochef.model.entity.RecetteAvecScore

interface ResultatsContract {

    interface View {
        fun afficherResultats(recettes: List<RecetteAvecScore>)
        fun afficherEtatVide()
        fun afficherNombreResultats(count: Int)
    }

    interface Presenter {
        fun chargerResultats(filtres: FiltreRecette, ingredientsDispos: List<IngredientQuantite>)
    }
}