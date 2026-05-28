package com.example.frigochef.view.adapter

import android.graphics.Color
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.frigochef.R
import com.example.frigochef.databinding.ItemDetailIngredientBinding
import com.example.frigochef.model.entity.IngredientQuantite
import com.example.frigochef.model.entity.RecetteIngredientDetail

/**
 * Adapter RecyclerView pour afficher les ingrédients d'une recette
 * dans l'écran de détail. Indique visuellement si chaque ingrédient
 * est possédé (en vert) ou manquant (en rouge) selon ingredientsDispos.
 * Si ingredientsDispos est vide (navigation depuis Accueil), les icônes sont cachées.
 */

class IngredientDetailAdapter : RecyclerView.Adapter<IngredientDetailAdapter.VH>() {

    private var ingredients       = listOf<RecetteIngredientDetail>()
    private var ingredientsDispos = listOf<IngredientQuantite>()

    fun soumettre(
        items:  List<RecetteIngredientDetail>,
        dispos: List<IngredientQuantite>
    ) {
        ingredients       = items
        ingredientsDispos = dispos
        // Notifie le RecyclerView que les données ont changé
        // Il va recréer et redessiner toutes les cartes visibles à l'écran
        notifyDataSetChanged()
    }

    /**
     * Code produit à l'aide de Claude
     *
     * ViewHolder contient une référence vers les vues d'une seule carte d'ingrédient.
     * RecyclerView crée un nombre limité de VH et les réutilise en les faisant défiler,
     * au lieu d'en créer un nouveau pour chaque ingrédient.
     *
     * binding est généré automatiquement par ViewBinding à partir du fichier XML
     * item_detail_ingredient.xml. Il donne accès à chaque vue par son ID sans avoir à appeler findViewById().
     */
    inner class VH(val binding: ItemDetailIngredientBinding) :
        RecyclerView.ViewHolder(binding.root)

    /**
     * Code produit à l'aide de Claude
     *
     * Appelé par RecyclerView quand il a besoin d'une nouvelle carte.
     * LayoutInflater convertit le fichier XML item_detail_ingredient.xml
     * en objet View que Android peut afficher à l'écran.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemDetailIngredientBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(binding)
    }

    override fun getItemCount() = ingredients.size

    /**
     * Code produit à l'aide de Claude
     *
     * Appelé par RecyclerView pour remplir une carte avec les données
     * de l'ingrédient à la position donnée.
     *
     * holder.binding donne accès aux vues de la carte,
     * holder est le VH réutilisé, et binding est son accès direct
     * aux éléments XML (tvNomIngredient, ivStatutIngredient, etc.)
     */
    override fun onBindViewHolder(holder: VH, position: Int) {
        val item    = ingredients[position]
        val context = holder.binding.root.context

        holder.binding.tvNomIngredient.text      = item.nom
        holder.binding.tvQuantiteIngredient.text = "${item.quantite} ${item.uniteMesure}"

        // ── Cache les icônes si navigation depuis Accueil (ingredientsDispos vide) ──
        if (ingredientsDispos.isEmpty()) {
            holder.binding.ivStatutIngredient.visibility = View.GONE
            holder.binding.viewIconBackground.visibility = View.GONE
            holder.binding.tvNomIngredient.setTextColor(
                ContextCompat.getColor(context, R.color.text_primary)
            )
            holder.binding.tvQuantiteIngredient.setTextColor(
                ContextCompat.getColor(context, R.color.text_secondary)
            )
            return
        }

        // Vérifie si l'utilisateur possède cet ingrédient
        // en comparant l'ID de l'ingrédient requis avec les IDs disponibles
        val possede = ingredientsDispos.any { it.ingredientId == item.ingredientId }

        if (possede) {
            holder.binding.ivStatutIngredient.visibility = View.VISIBLE
            holder.binding.viewIconBackground.visibility = View.VISIBLE
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
            holder.binding.ivStatutIngredient.visibility = View.VISIBLE
            holder.binding.viewIconBackground.visibility = View.VISIBLE
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