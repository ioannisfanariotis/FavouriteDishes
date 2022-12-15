package com.example.favouritedishes.models.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.favouritedishes.models.entities.Dish

@Database(entities = [Dish::class], version = 1)
abstract class DishRoomDatabase : RoomDatabase(){

    abstract fun dishDao(): DishDao
    companion object{
        @Volatile
        private var INSTANCE: DishRoomDatabase? = null

        fun getDatabase(context: Context): DishRoomDatabase{
            //if INSTANCE == null -> create it, else -> return it
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DishRoomDatabase::class.java,
                    "dish_database")
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}