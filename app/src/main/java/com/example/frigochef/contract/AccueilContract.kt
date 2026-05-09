package com.example.frigochef.contract

import com.example.frigochef.model.entity.Recette

interface AccueilContract {

    interface View {
        fun afficherRecettes(recettes: List<Recette>)
        fun afficherMessageVide()
        fun afficherErreur(message: String)
    }

    interface Presenter {
        fun chargerRecettes()
        fun rechercherRecettes(query: String)
    }
}