package com.example.favouritedishes.application

import android.app.Application
import com.example.favouritedishes.models.database.DishRepository
import com.example.favouritedishes.models.database.DishRoomDatabase

class DishApplication : Application() {

    private val database by lazy { DishRoomDatabase.getDatabase(this@DishApplication) }

    val repository by lazy { DishRepository(database.dishDao()) }
}