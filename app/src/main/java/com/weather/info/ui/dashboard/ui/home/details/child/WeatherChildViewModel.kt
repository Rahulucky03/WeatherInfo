package com.weather.info.ui.dashboard.ui.home.details.child

import androidx.lifecycle.MutableLiveData
import com.weather.info.base.viewmodel.BaseViewModel
import com.weather.info.data.AppDataManager
import com.weather.info.data.firebase.FireBaseAuthProvider
import com.weather.info.data.model.weather.Current
import com.weather.info.data.model.weather.DailyItem
import com.weather.info.data.model.weather.Weather
import com.weather.info.data.room.entity.History
import com.weather.info.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class WeatherChildViewModel @Inject constructor(
    override val appDataManager: AppDataManager,
    override val fireBaseAuthProvider: FireBaseAuthProvider
) : BaseViewModel(appDataManager, fireBaseAuthProvider) {

    val currentWeather = MutableLiveData<Current?>()
    val backgroundImage = MutableLiveData<String>()
    val title = MutableLiveData<String>()


    val weeklyWeather = MutableLiveData<List<DailyItem?>?>()


    fun getWeather(history: History?) {
        history?.let { it ->
            title.value = history.address
            compositeDisposable.add(
                appDataManager.fetchWeather(it.latLng.latitude, it.latLng.longitude)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : MyDisposableObserver<Weather>() {
                        override fun processResult(response: Response<Weather>) {
                            response.body()?.let {
                                //Weather
                                currentWeather.value = it.current
                                it.current?.weather?.let {
                                    if (it.isNotEmpty()) {
                                        val weatherItem = it[0]
                                        backgroundImage.value = String.format(
                                            Constants.WEATHER_IMAGE_BASE,
                                            weatherItem?.icon?.toString()
                                        )
                                    }
                                }

                                weeklyWeather.value = it.daily
                            }
                        }
                    })
            )
        }
    }

}