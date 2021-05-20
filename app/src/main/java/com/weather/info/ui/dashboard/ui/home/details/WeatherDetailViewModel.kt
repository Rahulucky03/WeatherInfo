package com.weather.info.ui.dashboard.ui.home.details

import com.google.android.gms.maps.model.LatLng
import com.weather.info.base.viewmodel.BaseViewModel
import com.weather.info.data.AppDataManager
import com.weather.info.data.firebase.FireBaseAuthProvider
import com.weather.info.data.model.Weather
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class WeatherDetailViewModel @Inject constructor(
    override val appDataManager: AppDataManager,
    override val fireBaseAuthProvider: FireBaseAuthProvider
) : BaseViewModel(appDataManager, fireBaseAuthProvider) {

    fun getWeather(latLng: LatLng?) {
        latLng?.let {
            compositeDisposable.add(
                appDataManager.fetchWeather(it.latitude, it.longitude)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : MyDisposableObserver<Weather>() {
                        override fun processResult(response: Response<Weather>) {
                            response.body()?.let {
                                //Weather
                                success.value = it.wind.speed.toString()
                            }
                        }
                    })
            )
        }
    }

}