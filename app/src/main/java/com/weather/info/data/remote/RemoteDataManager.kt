package com.weather.info.data.remote


import android.content.Context
import com.weather.info.data.model.Weather
import com.weather.info.data.pref.AppPreferenceManager
import com.weather.info.data.remote.service.ApiService
import com.weather.info.utils.Constants
import io.reactivex.Observable
import retrofit2.Response
import javax.inject.Inject

class RemoteDataManager @Inject constructor(
    val context: Context,
    val appPreferenceManager: AppPreferenceManager
) : RemoteSource {

    @Inject
    lateinit var apiService: ApiService

    override fun fetchWeather(lat: Double, lon: Double): Observable<Response<Weather>> {
        return apiService.fetchWeather(lat, lon, Constants.WEATHER_API)
    }


}