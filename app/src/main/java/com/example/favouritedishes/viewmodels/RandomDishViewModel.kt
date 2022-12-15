package com.example.favouritedishes.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.favouritedishes.models.entities.RandomDish
import com.example.favouritedishes.models.network.RandomDishApiService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class RandomDishViewModel : ViewModel() {

    private val service = RandomDishApiService()
    private val disposable = CompositeDisposable()
    val load = MutableLiveData<Boolean>()
    val response = MutableLiveData<RandomDish.Recipes>()
    val error = MutableLiveData<Boolean>()

    fun getRandom(){
        load.value = true
        disposable.add(service.getRandom()
                  .subscribeOn(Schedulers.newThread())
                  .observeOn(AndroidSchedulers.mainThread())
                  .subscribeWith(object: DisposableSingleObserver<RandomDish.Recipes>(){
                      override fun onSuccess(value: RandomDish.Recipes) {
                          load.value = false
                          response.value = value
                          error.value = false
                      }

                      override fun onError(e: Throwable) {
                          load.value = false
                          error.value = true
                          e.printStackTrace()
                      }
                  })
        )
    }
}