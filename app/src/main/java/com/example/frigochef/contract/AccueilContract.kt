package com.example.frigochef.contract

import com.example.frigochef.model.entity.Recette
import com.example.frigochef.model.entity.FiltreRecette

interface AccueilContract {

    interface View {
        fun afficherRecettes(recettes: List<Recette>)
        fun afficherMessageVide()
        fun afficherErreur(message: String)
    }

    interface Presenter {
        fun chargerRecettes()
        fun rechercherRecettes(query: String)
        fun filtrerParDifficulte(difficulte: String)
        fun filtrerParDiete(isVege: Boolean)
        fun filtrerParTemps(tempsMax: Int)
        fun filtrerParFiltres(filtres: FiltreRecette)  // ← ajoute
    }
}