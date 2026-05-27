package com.example.frigochef.view.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.frigochef.databinding.ItemSaisiIngredientBinding
import com.example.frigochef.model.entity.Ingredient
import com.example.frigochef.model.entity.IngredientQuantite

class IngredientSaisiAdapter(
    private val onQuantiteChange:(Long, Double) -> Unit,
    private val onSupprimer:(Long) -> Unit
) : RecyclerView.Adapter<IngredientSaisiAdapter.VH>(){

    private var items: List<IngredientQuantite> = emptyList()
    private var cache: Map<Long, Ingredient> = emptyMap()


    fun soumettre(nouvelles: List<IngredientQuantite>, nouveauCache: Map<Long, Ingredient>){
        items = nouvelles
        cache = nouveauCache
        notifyDataSetChanged()

    }

    inner class VH(val b: ItemSaisiIngredientBinding) :
        RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemSaisiIngredientBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int){
        val iq = items[position]
        val ingredient = cache[iq.ingredientId]

        holder.b.tvNomIngredient.text = ingredient?.nom ?: ""

        holder.b.etQuantite.setText(
            if(iq.quantite == iq.quantite.toLong().toDouble())
                iq.quantite.toLong().toString()
            else
                iq.quantite.toString()
        )



        holder.b.spinnerUnite.visibility   = View.GONE

        holder.b.dividerQtyUnit.visibility = View.VISIBLE
        holder.b.tvUnite.text = iq.unite

        // Écouter les changements de quantité
        holder.b.etQuantite.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int){}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int){}
            override fun afterTextChanged(s: Editable?){
                val valeur = s.toString().toDoubleOrNull() ?: return
                onQuantiteChange(iq.ingredientId, valeur)
            }
        })

        // clic pour supprimer l'ingrédient de la liste
        holder.b.root.setOnLongClickListener{
            onSupprimer(iq.ingredientId)
            true

        }

    }


}