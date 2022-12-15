package com.example.favouritedishes.views.adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.favouritedishes.R
import com.example.favouritedishes.databinding.RvItemDishBinding
import com.example.favouritedishes.models.entities.Dish
import com.example.favouritedishes.utils.Constants
import com.example.favouritedishes.views.activities.AddUpdateActivity
import com.example.favouritedishes.views.fragments.AllDishesFragment
import com.example.favouritedishes.views.fragments.FavouriteFragment

class DishAdapter(private val fragment: Fragment): RecyclerView.Adapter<DishAdapter.ViewHolder>() {

    private var dishes: List<Dish> = listOf()

    class ViewHolder(view: RvItemDishBinding): RecyclerView.ViewHolder(view.root){
        val ivDish = view.imDish
        val tvTitle = view.tvTitle
        val ibMore = view.ibMore
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RvItemDishBinding.inflate(LayoutInflater.from(fragment.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dish = dishes[position]
        Glide.with(fragment).load(dish.image).into(holder.ivDish)
        holder.tvTitle.text = dish.title
        holder.itemView.setOnClickListener{
            if (fragment is AllDishesFragment)
                fragment.dishDetails(dish)
            if (fragment is FavouriteFragment)
                fragment.dishDetails(dish)
        }
        holder.ibMore.setOnClickListener{
            val popup = PopupMenu(fragment.context, holder.ibMore)
            popup.menuInflater.inflate(R.menu.menu_dots, popup.menu)
            popup.setOnMenuItemClickListener {
                if (it.itemId == R.id.action_edit){
                    val intent = Intent(fragment.requireActivity(), AddUpdateActivity::class.java)
                    intent.putExtra(Constants.EXTRA_DETAILS, dish)
                    fragment.requireActivity().startActivity(intent)
                }else if (it.itemId == R.id.action_delete){
                    if (fragment is AllDishesFragment)
                        fragment.deleteDish(dish)
                }
                true
            }
            popup.show()
        }
        if (fragment is AllDishesFragment)
            holder.ibMore.visibility = View.VISIBLE
        else if (fragment is FavouriteFragment)
            holder.ibMore.visibility = View.GONE
    }

    override fun getItemCount(): Int {
        return dishes.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun dishesList(list: List<Dish>){
        dishes = list
        notifyDataSetChanged()
    }
}