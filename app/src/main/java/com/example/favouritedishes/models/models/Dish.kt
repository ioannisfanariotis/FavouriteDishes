package com.example.favouritedishes.models.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "dishes_table")
data class Dish (
    @ColumnInfo
    val image: String,

    @ColumnInfo(name = "image_source")
    val imageSource: String,

    @ColumnInfo
    val title: String,

    @ColumnInfo
    val type: String,

    @ColumnInfo
    val category: String,

    @ColumnInfo
    val ingredients: String,

    @ColumnInfo(name = "cooking_time")
    val cookingTime: String,

    @ColumnInfo
    val direction: String,

    @ColumnInfo(name = "favourite_dish")
    var fav: Boolean = false,

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
): Parcelable