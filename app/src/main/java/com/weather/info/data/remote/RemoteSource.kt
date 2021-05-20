package com.weather.info.data.remote

import com.weather.info.data.model.Weather
import io.reactivex.Observable
import retrofit2.Response

interface RemoteSource {
    fun fetchWeather(lat: Double, lon: Double): Observable<Response<Weather>>
}