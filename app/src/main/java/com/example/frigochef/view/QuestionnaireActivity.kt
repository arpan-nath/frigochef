package com.example.frigochef.view

import androidx.appcompat.app.AppCompatActivity
import com.example.frigochef.model.entity.FiltreRecette
import com.example.frigochef.model.entity.IngredientQuantite

class QuestionnaireActivity: AppCompatActivity(){

    // État partagé entre les fragments
    var cuisinesSelectionnees: List<String> = emptyList()
    var filtres: FiltreRecette = FiltreRecette()
    var ingredientsQuantites: MutableList<IngredientQuantite> = mutableListOf()
}