package com.example.favouritedishes.network

import com.example.favouritedishes.models.models.RandomDish
import com.example.favouritedishes.utils.Constants
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface Api {

    @GET(Constants.API_ENDPOINT)
    fun getRandom(@Query(Constants.API_KEY) apiKey: String,
                  @Query(Constants.LIMIT_LICENCE) limitLicence: Boolean,
                  @Query(Constants.TAGS) tags: String,
                  @Query(Constants.NUMBER) number: Int,
    ): Single<RandomDish.Recipes>
}