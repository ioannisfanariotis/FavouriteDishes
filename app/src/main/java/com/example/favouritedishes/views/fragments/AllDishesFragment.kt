package com.example.favouritedishes.views.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.favouritedishes.R
import com.example.favouritedishes.App
import com.example.favouritedishes.databinding.DialogListsBinding
import com.example.favouritedishes.databinding.FragmentAllDishesBinding
import com.example.favouritedishes.models.models.Dish
import com.example.favouritedishes.utils.Constants
import com.example.favouritedishes.views.activities.AddUpdateActivity
import com.example.favouritedishes.views.activities.MainActivity
import com.example.favouritedishes.views.adapters.DishAdapter
import com.example.favouritedishes.viewmodels.DishViewModel
import com.example.favouritedishes.viewmodels.DishViewModelFactory
import com.example.favouritedishes.views.adapters.ListsAdapter

class AllDishesFragment : Fragment() {

    private lateinit var binding: FragmentAllDishesBinding
    private lateinit var dishAdapter: DishAdapter
    private lateinit var listAdapter: ListsAdapter
    private lateinit var dialog: Dialog

    private val viewModel: DishViewModel by viewModels {
        DishViewModelFactory((requireActivity().application as App).repository)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentAllDishesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)
        binding.rvDishList.layoutManager = GridLayoutManager(requireActivity(), 2)
        dishAdapter = DishAdapter(this@AllDishesFragment)
        binding.rvDishList.adapter = dishAdapter
        observeVM()
    }

    private fun observeVM(){
        viewModel.allDishList.observe(viewLifecycleOwner){
                dishes ->
            dishes.let {
                if(it.isNotEmpty()) {
                    binding.rvDishList.visibility = View.VISIBLE
                    binding.tvNoDish.visibility = View.GONE
                    dishAdapter.dishesList(it)
                }else{
                    binding.rvDishList.visibility = View.GONE
                    binding.tvNoDish.visibility = View.VISIBLE
                }
            }
        }
    }

    fun deleteDish(dish: Dish){
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(resources.getString(R.string.delete_dish))
        builder.setMessage(resources.getString(R.string.sure))
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton(resources.getString(R.string.yes)){
            dialogInterface, _ ->
                viewModel.deleteDishDetails(dish)
                dialogInterface.dismiss()
        }
        builder.setNegativeButton(resources.getString(R.string.no)){
            dialogInterface, _ ->
                dialogInterface.dismiss()
        }
        val dialog: AlertDialog = builder.create()
        dialog.setCancelable(false)
        dialog.show()
    }

    private fun filterDialog(){
        dialog = Dialog(requireActivity())
        val binding = DialogListsBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)
        binding.tvTitle.text = resources.getString(R.string.select_filter)
        val dishTypes = Constants.dishTypes()
        dishTypes.add(0, Constants.ALL_ITEMS)
        binding.rvList.layoutManager = LinearLayoutManager(requireActivity())
        listAdapter = ListsAdapter(requireActivity(), this@AllDishesFragment, dishTypes, Constants.FILTER_SELECTION)
        binding.rvList.adapter = listAdapter
        dialog.show()
    }

    fun filterSelection(selectedFilter:String){
        dialog.dismiss()
        if (selectedFilter == Constants.ALL_ITEMS){
            viewModel.allDishList.observe(viewLifecycleOwner){
                    dishes ->
                        dishes.let {
                            if(it.isNotEmpty()) {
                                binding.rvDishList.visibility = View.VISIBLE
                                binding.tvNoDish.visibility = View.GONE
                                dishAdapter.dishesList(it)
                            }else{
                                binding.rvDishList.visibility = View.GONE
                                binding.tvNoDish.visibility = View.VISIBLE
                            }
                        }
            }
        }else{
            viewModel.filteredDishesList(selectedFilter).observe(viewLifecycleOwner){
                    dishes ->
                        dishes.let {
                            if(it.isNotEmpty()) {
                                binding.rvDishList.visibility = View.VISIBLE
                                binding.tvNoDish.visibility = View.GONE
                                dishAdapter.dishesList(it)
                            }else{
                                binding.rvDishList.visibility = View.GONE
                                binding.tvNoDish.visibility = View.VISIBLE
                            }
                        }
            }
        }
    }

    fun dishDetails(dish: Dish){
        findNavController().navigate(AllDishesFragmentDirections.actionAllDishesToDetails(dish))
        if (requireActivity() is MainActivity)
            (activity as MainActivity?)?.hideBottomNavigation()
    }

    override fun onResume() {
        super.onResume()
        if (requireActivity() is MainActivity)
            (activity as MainActivity?)?.showBottomNavigation()
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_add, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_add -> {
                startActivity(Intent(requireActivity(), AddUpdateActivity::class.java))
                return true
            }
            R.id.action_filter -> {
                filterDialog()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}