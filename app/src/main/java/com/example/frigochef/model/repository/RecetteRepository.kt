package com.example.frigochef.model.repository

import android.content.Context
import android.database.Cursor
import com.example.frigochef.database.FrigoDBHelper
import com.example.frigochef.model.entity.Ingredient
import com.example.frigochef.model.entity.Recette

class RecetteRepository(context: Context) {

    private val helper = FrigoDBHelper(context)

    // Toutes les recettes
    fun findAll(): List<Recette> {
        val db     = helper.readableDatabase
        val cursor = db.query(
            "recette",
            null,
            null, null, null, null,
            "nom ASC"
        )
        return cursor.use { parseRecettes(it) }
    }

    // Recherche par nom pour la barre de recherche AccueilActivity
    fun findParNom(query: String): List<Recette> {
        val db     = helper.readableDatabase
        val cursor = db.query(
            "recette",
            null,
            "nom LIKE ?",
            arrayOf("%$query%"),
            null, null,
            "nom ASC"
        )
        return cursor.use { parseRecettes(it) }
    }

    // Trouver une recette par ID
    fun findById(id: Long): Recette? {
        val db     = helper.readableDatabase
        val cursor = db.query(
            "recette",
            null,
            "id = ?",
            arrayOf(id.toString()),
            null, null, null
        )
        return cursor.use {
            if (it.moveToFirst()) cursorToRecette(it) else null
        }
    }

    // Filtrage pour ResultatsRecettesActivity
    fun findParFiltres(
        typeCuisine: String?,
        typeRepas:   String?,
        difficulte:  String?,
        tempsMax:    Int?,
        isVege:      Boolean?,
        isVegan:     Boolean?,
        isSansGluten:Boolean?
    ): List<Recette> {
        val conditions = mutableListOf<String>()
        val args       = mutableListOf<String>()

        typeCuisine?.let  { conditions += "type_cuisine = ?";      args += it }
        typeRepas?.let    { conditions += "type_repas = ?";         args += it }
        difficulte?.let   { conditions += "difficulte = ?";         args += it }
        tempsMax?.let     { conditions += "temps_prep <= ?";        args += it.toString() }
        isVege?.let       { if (it) { conditions += "is_vege = ?"; args += "1" } }
        isVegan?.let      { if (it) { conditions += "is_vegan = ?"; args += "1" } }
        isSansGluten?.let { if (it) { conditions += "is_sans_gluten = ?"; args += "1" } }

        val where  = if (conditions.isEmpty()) "" else "WHERE " + conditions.joinToString(" AND ")
        val db     = helper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM recette $where ORDER BY nom ASC",
            args.toTypedArray()
        )
        return cursor.use { parseRecettes(it) }
    }

    // Ingrédients d'une recette avec jointure — pour DetailRecetteActivity
    fun findIngredientsParRecette(recetteId: Long): List<Ingredient> {
        val db  = helper.readableDatabase
        val sql = """
            SELECT i.id, i.nom, i.categorie
            FROM ingredient i
            JOIN recette_ingredient ri ON ri.ingredient_id = i.id
            WHERE ri.recette_id = ?
            ORDER BY i.nom ASC
        """.trimIndent()
        val cursor = db.rawQuery(sql, arrayOf(recetteId.toString()))
        return cursor.use {
            val list = mutableListOf<Ingredient>()
            while (it.moveToNext()) {
                list.add(
                    Ingredient(
                        id        = it.getLong(it.getColumnIndexOrThrow("id")),
                        nom       = it.getString(it.getColumnIndexOrThrow("nom")),
                        categorie = it.getString(it.getColumnIndexOrThrow("categorie"))
                    )
                )
            }
            list
        }
    }

    // IDs des ingrédients d'une recette — pour calculer le score
    fun findIngredientIdsParRecette(recetteId: Long): List<Long> {
        val db     = helper.readableDatabase
        val cursor = db.query(
            "recette_ingredient",
            arrayOf("ingredient_id"),
            "recette_id = ?",
            arrayOf(recetteId.toString()),
            null, null, null
        )
        return cursor.use {
            val ids = mutableListOf<Long>()
            while (it.moveToNext()) ids.add(it.getLong(0))
            ids
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun parseRecettes(cursor: Cursor): List<Recette> {
        val list = mutableListOf<Recette>()
        while (cursor.moveToNext()) {
            list.add(cursorToRecette(cursor))
        }
        return list
    }

    private fun cursorToRecette(cursor: Cursor): Recette {
        return Recette(
            id           = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
            nom          = cursor.getString(cursor.getColumnIndexOrThrow("nom")),
            description  = cursor.getString(cursor.getColumnIndexOrThrow("description")),
            instructions = cursor.getString(cursor.getColumnIndexOrThrow("instructions")),
            tempsPrep    = cursor.getInt(cursor.getColumnIndexOrThrow("temps_prep")),
            difficulte   = cursor.getString(cursor.getColumnIndexOrThrow("difficulte")),
            typeCuisine  = cursor.getString(cursor.getColumnIndexOrThrow("type_cuisine")),
            typeRepas    = cursor.getString(cursor.getColumnIndexOrThrow("type_repas")),
            imageUrl     = cursor.getString(cursor.getColumnIndexOrThrow("image_url")),
            isVege       = cursor.getInt(cursor.getColumnIndexOrThrow("is_vege")) == 1,
            isVegan      = cursor.getInt(cursor.getColumnIndexOrThrow("is_vegan")) == 1,
            isSansGluten = cursor.getInt(cursor.getColumnIndexOrThrow("is_sans_gluten")) == 1
        )
    }
}