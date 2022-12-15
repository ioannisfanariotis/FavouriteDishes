package com.example.favouritedishes.models.network

import com.example.favouritedishes.models.entities.RandomDish
import com.example.favouritedishes.utils.Constants
import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class RandomDishApiService {

    private val api = Retrofit.Builder().baseUrl(Constants.BASE_URL)
                              .addConverterFactory(GsonConverterFactory.create())
                              .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                              .build().create(RandomDishAPI::class.java)

    fun getRandom(): Single<RandomDish.Recipes>{
        return api.getRandom(Constants.API_KEY_VALUE, Constants.LIMIT_LICENCE_VALUE, Constants.TAGS_VALUE, Constants.NUMBER_VALUE)
    }
}