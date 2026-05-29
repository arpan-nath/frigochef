package com.example.frigochef.view.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.frigochef.view.fragment.Etape1CuisineFragment
import com.example.frigochef.view.fragment.Etape2ContraintesFragment
import com.example.frigochef.view.fragment.Etape3IngredientsFragment
import com.example.frigochef.view.fragment.Etape4RecapFragment
/**
 * Adaptateur ViewPager2 qui instancie les 4 fragments du questionnaire selon leur position :
 * 0 → Etape1CuisineFragment, 1 → Etape2ContraintesFragment,
 * 2 → Etape3IngredientsFragment, 3 → Etape4RecapFragment.
 * Étend FragmentStateAdapter pour gérer le cycle de vie de chaque fragment
 * et libérer la mémoire des fragments hors écran.
 */
class QuestionnairePagerAdapter(activity: FragmentActivity): FragmentStateAdapter(activity){

    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment = when (position){
        0 -> Etape1CuisineFragment()
        1 -> Etape2ContraintesFragment()
        2 -> Etape3IngredientsFragment()
        3 -> Etape4RecapFragment()
        else -> throw IllegalArgumentException("Position invalide: $position")

    }
}