package com.example.frigochef.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.frigochef.model.entity.FiltreRecette
import com.example.frigochef.model.entity.IngredientQuantite

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TEST TEMPORAIRE — lancer ResultatsActivity directement
        val intent = Intent(this, ResultatsActivity::class.java).apply {
            putExtra("filtres", FiltreRecette())
            putExtra("ingredients", ArrayList(listOf(
                IngredientQuantite(1L, 3.0, "gousse"),
                IngredientQuantite(2L, 2.0, "unité"),
                IngredientQuantite(17L, 300.0, "g")
            )))
        }
        startActivity(intent)
        finish() // fermer MainActivity pour ne pas y revenir
    }
}