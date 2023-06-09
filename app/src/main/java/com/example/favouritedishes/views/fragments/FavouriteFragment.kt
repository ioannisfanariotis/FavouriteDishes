package com.example.favouritedishes.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.favouritedishes.App
import com.example.favouritedishes.databinding.FragmentFavouriteBinding
import com.example.favouritedishes.models.models.Dish
import com.example.favouritedishes.viewmodels.DishViewModel
import com.example.favouritedishes.viewmodels.DishViewModelFactory
import com.example.favouritedishes.views.activities.MainActivity
import com.example.favouritedishes.views.adapters.DishAdapter

class FavouriteFragment : Fragment() {

    private var binding: FragmentFavouriteBinding? = null
    private lateinit var dishAdapter: DishAdapter
    private val viewModel: DishViewModel by viewModels {
        DishViewModelFactory((requireActivity().application as App).repository)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentFavouriteBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeVM()
    }

    private fun observeVM(){
        viewModel.favDishList.observe(viewLifecycleOwner){
                dishes ->
            dishes.let {
                binding!!.rvFavDishList.layoutManager = GridLayoutManager(requireActivity(), 2)
                dishAdapter = DishAdapter(this)
                binding!!.rvFavDishList.adapter = dishAdapter
                if(it.isNotEmpty()) {
                    binding!!.rvFavDishList.visibility = View.VISIBLE
                    binding!!.tvNoFavDish.visibility = View.GONE
                    dishAdapter.dishesList(it)
                }else{
                    binding!!.rvFavDishList.visibility = View.GONE
                    binding!!.tvNoFavDish.visibility = View.VISIBLE
                }
            }
        }
    }

    fun dishDetails(dish: Dish){
        findNavController().navigate(FavouriteFragmentDirections.actionFavouriteDishesToDetails(dish))
        if (requireActivity() is MainActivity)
            (activity as MainActivity?)?.hideBottomNavigation()
    }

    override fun onResume() {
        super.onResume()
        if (requireActivity() is MainActivity)
            (activity as MainActivity?)?.showBottomNavigation()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}