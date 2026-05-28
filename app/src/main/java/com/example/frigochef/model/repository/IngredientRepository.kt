package com.example.frigochef.model.repository

import android.content.Context
import android.database.Cursor
import com.example.frigochef.database.FrigoDBHelper
import com.example.frigochef.model.entity.Ingredient

/**
 * Fournit l'accès aux données de la table ingredient.
 * Toutes les opérations sont en lecture seule — les ingrédients
 * sont pré-chargés et ne peuvent pas être modifiés par l'utilisateur.
 */

class IngredientRepository(context: Context) {

    private val helper = FrigoDBHelper(context)

    fun findAll(): List<Ingredient> {
        val db     = helper.readableDatabase
        val cursor = db.query(
            "ingredient",
            null,
            null, null, null, null,
            "nom ASC"
        )
        return cursor.use { parseIngredients(it) }
    }

    fun findParNom(query: String): List<Ingredient> {
        val db     = helper.readableDatabase
        val cursor = db.query(
            "ingredient",
            null,
            "nom LIKE ?",
            arrayOf("%$query%"),
            null, null,
            "nom ASC",
            "20"
        )
        return cursor.use { parseIngredients(it) }
    }

    fun findById(id: Long): Ingredient? {
        val db     = helper.readableDatabase
        val cursor = db.query(
            "ingredient",
            null,
            "id = ?",
            arrayOf(id.toString()),
            null, null, null
        )
        return cursor.use {
            if (it.moveToFirst()) cursorToIngredient(it) else null
        }
    }

    fun findByIds(ids: List<Long>): List<Ingredient> {
        if (ids.isEmpty()) return emptyList()
        val db          = helper.readableDatabase
        val placeholders = ids.joinToString(",") { "?" }
        val cursor      = db.rawQuery(
            "SELECT * FROM ingredient WHERE id IN ($placeholders) ORDER BY nom ASC",
            ids.map { it.toString() }.toTypedArray()
        )
        return cursor.use { parseIngredients(it) }
    }

    private fun parseIngredients(cursor: Cursor): List<Ingredient> {
        val list = mutableListOf<Ingredient>()
        while (cursor.moveToNext()) {
            list.add(cursorToIngredient(cursor))
        }
        return list
    }

    private fun cursorToIngredient(cursor: Cursor): Ingredient {
        return Ingredient(
            id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
            nom = cursor.getString(cursor.getColumnIndexOrThrow("nom")),
            categorie = cursor.getString(cursor.getColumnIndexOrThrow("categorie"))
        )
    }
}