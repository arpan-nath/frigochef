package com.example.frigochef.model

import android.content.Context
import com.example.frigochef.model.entity.FiltreRecette

/**
 * Gestionnaire des préférences légères de l'application.
 * Sauvegarde et restaure les filtres du Questionnaire entre les sessions.
 */
class PrefsManager(context: Context) {

    private val prefs = context.getSharedPreferences("frigochef_prefs", Context.MODE_PRIVATE)

    fun sauvegarderFiltres(filtres: FiltreRecette, cuisines: List<String>) {
        prefs.edit().apply {
            putString("type_repas",          filtres.typeRepas)
            putString("difficulte",          filtres.difficulte)
            putInt("temps_max",              filtres.tempsMax ?: -1)
            putBoolean("is_vege",            filtres.isVege)
            putBoolean("is_vegan",           filtres.isVegan)
            putBoolean("is_sans_gluten",     filtres.isSansGluten)
            putString("cuisines",            cuisines.joinToString(","))
            apply()
        }
    }

    fun chargerFiltres(): FiltreRecette {
        return FiltreRecette(
            typeRepas    = prefs.getString("type_repas",  null),
            difficulte   = prefs.getString("difficulte",  null),
            tempsMax     = prefs.getInt("temps_max", -1).takeIf { it != -1 },
            isVege       = prefs.getBoolean("is_vege",        false),
            isVegan      = prefs.getBoolean("is_vegan",       false),
            isSansGluten = prefs.getBoolean("is_sans_gluten", false)
        )
    }

    fun chargerCuisines(): List<String> {
        val raw = prefs.getString("cuisines", "") ?: ""
        return if (raw.isEmpty()) emptyList() else raw.split(",")
    }

    fun reinitialiser() {
        prefs.edit().clear().apply()
    }
}