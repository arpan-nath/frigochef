package com.example.frigochef.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.frigochef.model.entity.FiltreRecette

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TEST TEMPORAIRE — lancer ResultatsActivity directement
        val intent = Intent(this, ResultatsActivity::class.java).apply {
            putExtra("filtres", FiltreRecette())
            putExtra("ingredients", longArrayOf(1, 2, 3, 4, 5))
        }
        startActivity(intent)
        finish() // fermer MainActivity pour ne pas y revenir
    }
}