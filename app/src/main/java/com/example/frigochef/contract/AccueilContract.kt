package com.example.frigochef.contract

import com.example.frigochef.model.entity.FiltreRecette
import com.example.frigochef.model.entity.Recette

interface AccueilContract {

    interface View {
        fun afficherRecettes(recettes: List<Recette>)
        fun afficherMessageVide()
        fun afficherErreur(message: String)
        fun afficherChipsSession(noms: List<String>)
    }

    interface Presenter {
        fun chargerRecettes()
        fun rechercherRecettes(query: String)
        fun filtrerParFiltres(filtres: FiltreRecette)
        fun chargerSessionIngredients()
    }
}