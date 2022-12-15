package com.example.favouritedishes.views.fragments

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.favouritedishes.R
import com.example.favouritedishes.application.DishApplication
import com.example.favouritedishes.databinding.FragmentRandomBinding
import com.example.favouritedishes.models.entities.Dish
import com.example.favouritedishes.models.entities.RandomDish
import com.example.favouritedishes.utils.Constants
import com.example.favouritedishes.viewmodels.DishViewModel
import com.example.favouritedishes.viewmodels.DishViewModelFactory
import com.example.favouritedishes.viewmodels.RandomDishViewModel

class RandomFragment : Fragment() {

    private var binding: FragmentRandomBinding? = null
    private lateinit var viewModel: RandomDishViewModel
    private var dialog: Dialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentRandomBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[RandomDishViewModel::class.java]
        viewModel.getRandom()

        observeVM()
        binding!!.swipeRefreshLl.setOnRefreshListener {
            viewModel.getRandom()
        }
    }

    private fun observeVM(){
        viewModel.response.observe(viewLifecycleOwner) {
                response ->
                    response?.let {
                        if (binding!!.swipeRefreshLl.isRefreshing)
                            binding!!.swipeRefreshLl.isRefreshing = false
                        setViews(response.recipes[0])
                    }
        }

        viewModel.error.observe(viewLifecycleOwner) {
                error ->
                    error?.let {
                        if (binding!!.swipeRefreshLl.isRefreshing)
                            binding!!.swipeRefreshLl.isRefreshing = false
                    }
        }

        viewModel.load.observe(viewLifecycleOwner) {
                dish ->
                    dish?.let {
                        if (dish && !binding!!.swipeRefreshLl.isRefreshing)
                            showDialog()
                        else
                            hideDialog()
                    }
        }
    }

    private fun setViews(recipe: RandomDish.Recipe){
        var favAdded = false
        binding!!.ivHeart.setImageDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.ic_favourite_unselected))

        Glide.with(requireActivity()).load(recipe.image).centerCrop().into(binding!!.ivDishImage)
        binding!!.tvTitle.text = recipe.title
        var type = "other"
        if (recipe.dishTypes.isNotEmpty()){
            type = recipe.dishTypes[0]
            binding!!.tvType.text = type
        }
        binding!!.tvCategory.text = "Other"
        var ingredients = ""
        for (value in recipe.extendedIngredients){
            ingredients = if (ingredients.isEmpty())
                value.original
            else
                ingredients + ", \n" + value.original
        }
        binding!!.tvIngredients.text = ingredients
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            binding!!.tvCookingDirection.text = Html.fromHtml(recipe.instructions, Html.FROM_HTML_MODE_COMPACT)
        else
            binding!!.tvCookingDirection.text = Html.fromHtml(recipe.instructions)
        binding!!.tvCookingTime.text = resources.getString(R.string.estimate_cooking_time, recipe.readyInMinutes.toString())

        binding!!.ivHeart.setOnClickListener {

            if (favAdded)
                Toast.makeText(requireActivity(), resources.getString(R.string.already), Toast.LENGTH_SHORT).show()
            else{
                val randomDishDetails = Dish(
                    recipe.image,
                    Constants.SOURCE_ONLINE,
                    recipe.title,
                    type,
                    "Other",
                    ingredients,
                    recipe.readyInMinutes.toString(),
                    recipe.instructions,
                    true
                )
                val dishViewModel: DishViewModel by viewModels {
                    DishViewModelFactory((requireActivity().application as DishApplication).repository)
                }
                dishViewModel.insertDishData(randomDishDetails)
                favAdded = true
                binding!!.ivHeart.setImageDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.ic_favourite_selected))
                Toast.makeText(requireActivity(), resources.getString(R.string.favourite_added), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDialog(){
        dialog = Dialog(requireActivity())
        dialog?.let {
            it.setContentView(R.layout.dialog_loading)
            it.show()
        }
    }

    private fun hideDialog(){
        dialog?.dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}