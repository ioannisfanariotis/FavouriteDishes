package com.example.favouritedishes.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.favouritedishes.models.models.Dish
import kotlinx.coroutines.flow.Flow

@Dao
interface DishDao {

    @Insert
    suspend fun insertDishDetails(dish: Dish)

    @Query("SELECT * FROM dishes_table ORDER BY ID")
    fun getAllDishesList(): Flow<List<Dish>>

    @Update
    suspend fun updateDishDetails(dish: Dish)

    @Query("SELECT * FROM dishes_table WHERE favourite_dish = 1")
    fun getFavDishesList(): Flow<List<Dish>>

    @Delete
    suspend fun deleteDishDetails(dish: Dish)

    @Query("SELECT * FROM dishes_table WHERE type = :filterType")
    fun getFilteredDishesList(filterType: String): Flow<List<Dish>>
}