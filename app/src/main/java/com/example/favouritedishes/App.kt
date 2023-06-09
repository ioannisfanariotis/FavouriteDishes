package com.example.favouritedishes

import android.app.Application
import com.example.favouritedishes.repository.DishRepository
import com.example.favouritedishes.database.AppDatabase

class App : Application() {

    private val database by lazy { AppDatabase.getDatabase(this@App) }

    val repository by lazy { DishRepository(database.dishDao()) }
}