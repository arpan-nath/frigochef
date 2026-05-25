package com.example.frigochef.view.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.frigochef.R

class Etape2ContraintesFragment: Fragment(R.layout.fragment_etape2_questionnaire){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // TODO : connecter les chips et le slider
    }

    // Lire les chips cochées type de repas (chipDejeuner, chipDiner, chipSouper, chipCollation)
    // Lire les chips cochées difficulté (chipFacile, chipMoyen, chipDifficile)
    // Lire les chips cochées régime (chipVegetarien, chipVegan, chipSansGluten)
    // Mettre à jour tvTempsMaxValeur en temps réel quand le slider bouge
    // Synchroniser tout ça dans QuestionnaireActivity.filtres à chaque changement
    // Restaurer les valeurs au retour arrière
}