package com.example.frigochef.model.repository

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.frigochef.database.FrigoDBHelper
import com.example.frigochef.model.entity.SessionIngredient

class SessionRepository(context: Context) {

    private val helper = FrigoDBHelper(context)

    fun findAll(): List<SessionIngredient> {
        val db     = helper.readableDatabase
        val cursor = db.query(
            "session_ingredients",
            null,
            null, null, null, null,
            "frequence_usage DESC"
        )
        return cursor.use { parseSession(it) }
    }

    fun findAllIds(): List<Long> {
        val db     = helper.readableDatabase
        val cursor = db.query(
            "session_ingredients",
            arrayOf("ingredient_id"),
            null, null, null, null,
            "frequence_usage DESC"
        )
        return cursor.use {
            val ids = mutableListOf<Long>()
            while (it.moveToNext()) {
                ids.add(it.getLong(0))
            }
            ids
        }
    }

    fun upsert(ingredientId: Long) {
        val db        = helper.writableDatabase
        val timestamp = System.currentTimeMillis()

        val cursor = db.query(
            "session_ingredients",
            arrayOf("id"),
            "ingredient_id = ?",
            arrayOf(ingredientId.toString()),
            null, null, null
        )

        val existe = cursor.use { it.moveToFirst() }

        if (existe) {
            db.execSQL(
                "UPDATE session_ingredients SET frequence_usage = frequence_usage + 1, " +
                        "date_derniere_saisie = ? WHERE ingredient_id = ?",
                arrayOf(timestamp, ingredientId.toString())
            )
        } else {
            val cv = ContentValues().apply {
                put("ingredient_id",        ingredientId)
                put("date_derniere_saisie", timestamp)
                put("frequence_usage",      1)
            }
            db.insert("session_ingredients", null, cv)
        }
    }

    fun clearSession() {
        helper.writableDatabase.delete("session_ingredients", null, null)
    }

    private fun parseSession(cursor: Cursor): List<SessionIngredient> {
        val list = mutableListOf<SessionIngredient>()
        while (cursor.moveToNext()) {
            list.add(cursorToSession(cursor))
        }
        return list
    }

    private fun cursorToSession(cursor: Cursor): SessionIngredient {
        return SessionIngredient(
            id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
            ingredientId = cursor.getLong(cursor.getColumnIndexOrThrow("ingredient_id")),
            dateDerniereSaisie = cursor.getLong(cursor.getColumnIndexOrThrow("date_derniere_saisie")),
            frequenceUsage = cursor.getInt(cursor.getColumnIndexOrThrow("frequence_usage"))
        )
    }
}