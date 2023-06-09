package com.example.favouritedishes.viewmodels

import androidx.lifecycle.*
import com.example.favouritedishes.repository.DishRepository
import com.example.favouritedishes.models.models.Dish
import kotlinx.coroutines.launch

class DishViewModel(private val repo: DishRepository):ViewModel() {

    fun insertDishData(dish: Dish) = viewModelScope.launch {
        repo.insertDishData(dish)
    }

    val allDishList: LiveData<List<Dish>> = repo.allDishList.asLiveData()

    fun updateDishDetails(dish: Dish) = viewModelScope.launch {
        repo.updateDishDetails(dish)
    }

    val favDishList: LiveData<List<Dish>> = repo.favDishList.asLiveData()

    fun deleteDishDetails(dish: Dish) = viewModelScope.launch {
        repo.deleteDishDetails(dish)
    }

    fun filteredDishesList(value: String): LiveData<List<Dish>> = repo.filteredDishesList(value).asLiveData()
}

class DishViewModelFactory(private val repo: DishRepository):ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DishViewModel::class.java))
            @Suppress("UNCHECKED_CAST")
            return DishViewModel(repo) as T
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}