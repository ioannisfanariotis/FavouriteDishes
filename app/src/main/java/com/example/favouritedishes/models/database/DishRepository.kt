package com.example.favouritedishes.models.database

import androidx.annotation.WorkerThread
import com.example.favouritedishes.models.entities.Dish
import kotlinx.coroutines.flow.Flow

class DishRepository(private val dishDao: DishDao ) {

    @WorkerThread
    suspend fun insertDishData(dish: Dish){
        dishDao.insertDishDetails(dish)
    }

    val allDishList: Flow<List<Dish>> = dishDao.getAllDishesList()

    @WorkerThread
    suspend fun updateDishDetails(dish: Dish){
        dishDao.updateDishDetails(dish)
    }

    val favDishList: Flow<List<Dish>> = dishDao.getFavDishesList()

    @WorkerThread
    suspend fun deleteDishDetails(dish: Dish){
        dishDao.deleteDishDetails(dish)
    }

    fun filteredDishesList(value: String): Flow<List<Dish>> = dishDao.getFilteredDishesList(value)
}