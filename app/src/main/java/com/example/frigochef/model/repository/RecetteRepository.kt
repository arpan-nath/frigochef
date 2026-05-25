package com.example.frigochef.model.repository

import android.content.Context
import android.database.Cursor
import com.example.frigochef.database.FrigoDBHelper
import com.example.frigochef.model.entity.FiltreRecette
import com.example.frigochef.model.entity.Ingredient
import com.example.frigochef.model.entity.IngredientQuantite
import com.example.frigochef.model.entity.Recette
import com.example.frigochef.model.entity.RecetteIngredientDetail

class RecetteRepository(context: Context) {

    private val helper = FrigoDBHelper(context)

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

    fun findParFiltres(filtres: FiltreRecette): List<Recette> {
        val conditions = mutableListOf<String>()
        val args       = mutableListOf<String>()

        filtres.typeCuisine?.let  { conditions += "type_cuisine = ?";  args += it }
        filtres.typeRepas?.let    { conditions += "type_repas = ?";     args += it }
        filtres.difficulte?.let   { conditions += "difficulte = ?";     args += it }
        filtres.tempsMax?.let     { conditions += "temps_prep <= ?";    args += it.toString() }
        if (filtres.isVege)       { conditions += "is_vege = ?";        args += "1" }
        if (filtres.isVegan)      { conditions += "is_vegan = ?";       args += "1" }
        if (filtres.isSansGluten) { conditions += "is_sans_gluten = ?"; args += "1" }

        val where  = if (conditions.isEmpty()) "" else "WHERE " + conditions.joinToString(" AND ")
        val cursor = helper.readableDatabase.rawQuery(
            "SELECT * FROM recette $where ORDER BY nom ASC",
            args.toTypedArray()
        )
        return cursor.use { parseRecettes(it) }
    }

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
            isSansGluten = cursor.getInt(cursor.getColumnIndexOrThrow("is_sans_gluten")) == 1,
            portions     = cursor.getInt(cursor.getColumnIndexOrThrow("portions"))
        )
    }

    fun findIngredientQuantitesParRecette(recetteId: Long): List<IngredientQuantite> {
        val db  = helper.readableDatabase
        val sql = """
        SELECT ri.ingredient_id, ri.quantite, ri.unite_mesure
        FROM recette_ingredient ri
        WHERE ri.recette_id = ?
    """.trimIndent()
        val cursor = db.rawQuery(sql, arrayOf(recetteId.toString()))
        return cursor.use {
            val list = mutableListOf<IngredientQuantite>()
            while (it.moveToNext()) {
                list.add(
                    IngredientQuantite(
                        ingredientId = it.getLong(it.getColumnIndexOrThrow("ingredient_id")),
                        quantite = it.getDouble(it.getColumnIndexOrThrow("quantite")),
                        unite = it.getString(it.getColumnIndexOrThrow("unite_mesure"))
                    )
                )
            }
            list
        }
    }

    fun findIngredientsDetailParRecette(recetteId: Long): List<RecetteIngredientDetail> {
        val db  = helper.readableDatabase
        val sql = """
        SELECT i.id, i.nom, ri.quantite, ri.unite_mesure
        FROM ingredient i
        JOIN recette_ingredient ri ON ri.ingredient_id = i.id
        WHERE ri.recette_id = ?
        ORDER BY i.nom ASC
    """.trimIndent()
        val cursor = db.rawQuery(sql, arrayOf(recetteId.toString()))
        return cursor.use {
            val list = mutableListOf<RecetteIngredientDetail>()
            while (it.moveToNext()) {
                list.add(RecetteIngredientDetail(
                    ingredientId = it.getLong(it.getColumnIndexOrThrow("id")),
                    nom          = it.getString(it.getColumnIndexOrThrow("nom")),
                    quantite     = it.getString(it.getColumnIndexOrThrow("quantite")) ?: "",
                    uniteMesure  = it.getString(it.getColumnIndexOrThrow("unite_mesure")) ?: ""
                ))
            }
            list
        }
    }
}