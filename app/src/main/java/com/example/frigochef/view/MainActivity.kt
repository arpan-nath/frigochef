package com.example.frigochef.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.frigochef.model.entity.FiltreRecette
import com.example.frigochef.model.entity.IngredientQuantite

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val ingredients = ArrayList<IngredientQuantite>().apply {
            add(IngredientQuantite(1L,  3.0,  "gousse"))
            add(IngredientQuantite(2L,  2.0,  "unité"))
            add(IngredientQuantite(17L, 300.0, "g"))
        }

        val intent = Intent(this, ResultatsActivity::class.java).apply {
            putExtra("filtres",     FiltreRecette())
            putExtra("ingredients", ingredients)
        }
        startActivity(intent)
        finish()
    }
}