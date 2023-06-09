package com.example.favouritedishes.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.favouritedishes.models.models.Dish

@Database(entities = [Dish::class], version = 1)
abstract class AppDatabase : RoomDatabase(){

    abstract fun dishDao(): DishDao
    companion object{
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            //if INSTANCE == null -> create it, else -> return it
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "dish_database")
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}