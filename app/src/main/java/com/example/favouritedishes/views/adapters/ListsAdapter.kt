package com.example.favouritedishes.views.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.favouritedishes.databinding.RvListsBinding
import com.example.favouritedishes.views.activities.AddUpdateActivity
import com.example.favouritedishes.views.fragments.AllDishesFragment

class ListsAdapter (private val activity: Activity, private val fragment: Fragment?, private val listItems: List<String>,
                    private val selection: String): RecyclerView.Adapter<ListsAdapter.ViewHolder>(){

     class ViewHolder(view: RvListsBinding): RecyclerView.ViewHolder(view.root){
         val tvText = view.tvText
     }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RvListsBinding.inflate(LayoutInflater.from(activity), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listItems[position]
        holder.tvText.text = item
        holder.itemView.setOnClickListener{
            if (activity is AddUpdateActivity)
                activity.selectedListItem(item, selection)
            if (fragment is AllDishesFragment)
                fragment.filterSelection(item)
        }
    }

    override fun getItemCount(): Int {
        return listItems.size
    }
}