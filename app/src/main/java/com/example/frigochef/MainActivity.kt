package com.example.frigochef

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.frigochef.model.entity.IngredientQuantite
import com.example.frigochef.view.DetailRecetteActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val ingredients = ArrayList<IngredientQuantite>().apply {
            add(IngredientQuantite(14L, 400.0, "g"))     // Pois chiches
            add(IngredientQuantite(1L,  2.0,  "gousse")) // Ail
            add(IngredientQuantite(52L, 1.0,  "unité"))  // Citron
            // Huile d'olive manquante intentionnellement
        }

        val intent = Intent(this, DetailRecetteActivity::class.java).apply {
            putExtra("recette_id",  1L)   // ID 1 = Houmous
            putExtra("score",       75)
            putExtra("ingredients", ingredients)
        }
        startActivity(intent)
        finish()
    }
}