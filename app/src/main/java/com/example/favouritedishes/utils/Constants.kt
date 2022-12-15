package com.example.favouritedishes.utils

object Constants {

    const val TYPE = "DishType"
    const val CATEGORY = "DishCategory"
    const val TIME = "DishCookingTime"

    const val SOURCE_ONLINE = "Online"
    const val SOURCE_LOCAL = "Local"

    const val EXTRA_DETAILS = "Details"

    const val ALL_ITEMS = "All"
    const val FILTER_SELECTION = "FilterSelection"

    const val API_ENDPOINT = "recipes/random"

    const val API_KEY = "apiKey"
    const val LIMIT_LICENCE = "limitLicence"
    const val TAGS = "tags"
    const val NUMBER = "number"

    const val BASE_URL = "https://api.spoonacular.com/"

    const val API_KEY_VALUE = "47cdc3ea4c9e429aa93b0a425fd137ca"
    const val LIMIT_LICENCE_VALUE = true
    const val TAGS_VALUE = "dessert"
    const val NUMBER_VALUE = 1

    const val NOTIFICATION_ID = "notification_id"
    const val NOTIFICATION_NAME = "Dish"
    const val NOTIFICATION_CHANNEL = "channel_01"

    fun dishTypes():ArrayList<String>{
        val list = ArrayList<String>()
        list.add("Breakfast")
        list.add("Lunch")
        list.add("Snack")
        list.add("Dinner")
        return list
    }
    fun dishCategories():ArrayList<String>{
        val list = ArrayList<String>()
        list.add("Pizza")
        list.add("Burger")
        list.add("Crepe")
        list.add("Hot Dog")
        list.add("Sandwich")
        list.add("Salad")
        list.add("Dessert")
        list.add("Other")
        return list
    }

    fun dishCookingTime():ArrayList<String>{
        val list = ArrayList<String>()
        list.add("5")
        list.add("10")
        list.add("15")
        list.add("20")
        list.add("30")
        list.add("45")
        list.add("60")
        list.add("90")
        list.add("120")
        return list
    }
}