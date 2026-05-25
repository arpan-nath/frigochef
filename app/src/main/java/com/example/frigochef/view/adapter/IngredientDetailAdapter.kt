package com.example.frigochef.view.adapter

import android.graphics.Color
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.frigochef.R
import com.example.frigochef.databinding.ItemDetailIngredientBinding
import com.example.frigochef.model.entity.IngredientQuantite
import com.example.frigochef.model.entity.RecetteIngredientDetail

class IngredientDetailAdapter : RecyclerView.Adapter<IngredientDetailAdapter.VH>() {

    private var ingredients       = listOf<RecetteIngredientDetail>()
    private var ingredientsDispos = listOf<IngredientQuantite>()

    fun soumettre(
        items:  List<RecetteIngredientDetail>,
        dispos: List<IngredientQuantite>
    ) {
        ingredients       = items
        ingredientsDispos = dispos
        notifyDataSetChanged()
    }

    inner class VH(val binding: ItemDetailIngredientBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemDetailIngredientBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(binding)
    }

    override fun getItemCount() = ingredients.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item    = ingredients[position]
        val context = holder.binding.root.context

        // Nom
        holder.binding.tvNomIngredient.text = item.nom

        // Quantité + unité
        holder.binding.tvQuantiteIngredient.text = "${item.quantite} ${item.uniteMesure}"

        // Statut — possédé ou manquant
        val possede = ingredientsDispos.any { it.ingredientId == item.ingredientId }

        if (possede) {
            holder.binding.ivStatutIngredient.setImageResource(R.drawable.ic_check)
            holder.binding.ivStatutIngredient.setColorFilter(
                ContextCompat.getColor(context, R.color.ingredient_ok_icon)
            )
            holder.binding.viewIconBackground.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(context, R.color.ingredient_ok_bg))
            holder.binding.tvNomIngredient.setTextColor(
                ContextCompat.getColor(context, R.color.text_primary)
            )
            holder.binding.tvQuantiteIngredient.setTextColor(
                ContextCompat.getColor(context, R.color.text_secondary)
            )
        } else {
            holder.binding.ivStatutIngredient.setImageResource(R.drawable.ic_close)
            holder.binding.ivStatutIngredient.setColorFilter(
                ContextCompat.getColor(context, R.color.ingredient_miss_icon)
            )
            holder.binding.viewIconBackground.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(context, R.color.ingredient_miss_bg))
            holder.binding.tvNomIngredient.setTextColor(
                ContextCompat.getColor(context, R.color.text_secondary)
            )
            holder.binding.tvQuantiteIngredient.setTextColor(
                ContextCompat.getColor(context, R.color.text_secondary)
            )
        }
    }
}