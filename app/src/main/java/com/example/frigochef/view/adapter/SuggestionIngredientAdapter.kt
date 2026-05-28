package com.example.frigochef.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.frigochef.databinding.ItemSuggestionIngredientBinding
import com.example.frigochef.model.entity.Ingredient

class SuggestionIngredientAdapter(
    private val onAjout: (Ingredient) -> Unit
): RecyclerView.Adapter<SuggestionIngredientAdapter.VH>(){

    private var items: List<Ingredient> = emptyList()

    fun soumettre(nouvelles: List<Ingredient>){
        items = nouvelles
        notifyDataSetChanged()
    }

    inner class VH(val b: ItemSuggestionIngredientBinding) :
        RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemSuggestionIngredientBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int){
        val ingredient = items[position]

        holder.b.tvNomIngredient.text = ingredient.nom
        holder.b.tvCategorieIngredient.text = ingredient.categorie
        holder.b.root.setOnClickListener{onAjout(ingredient)}
        holder.b.tvBtnAjouter.visibility = View.GONE
    }

}