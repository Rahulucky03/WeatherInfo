/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.weather.info.data.remote.service

import com.weather.info.data.model.weather.Weather
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * REST API access points
 */
interface ApiService {

    //https://api.openweathermap.org/data/2.5/onecall?lat=33.44&lon=-94.04&exclude=minutely,hourly&appid=dec60757a99f4d987ffb0ab9119aa466
    //https://openweathermap.org/api/one-call-api

    //Image loading
    //http://openweathermap.org/img/wn/10d@2x.png

    /*@GET("/data/2.5/weather")
    fun fetchWeatherTest(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String
    ): Observable<Response<Weather>>*/

    @GET("onecall")
    fun fetchWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String = "dec60757a99f4d987ffb0ab9119aa466",
        @Query("exclude") exclude: String = "minutely,hourly"
    ): Observable<Response<Weather>>
}
