package com.weather.info.data.model.weather

import com.google.gson.annotations.SerializedName

data class Current(

    @field:SerializedName("sunrise")
    val sunrise: Int? = null,

    @field:SerializedName("temp")
    val temp: Double? = null,

    @field:SerializedName("visibility")
    val visibility: Int? = null,

    @field:SerializedName("uvi")
    val uvi: Double? = null,

    @field:SerializedName("pressure")
    val pressure: Int? = null,

    @field:SerializedName("clouds")
    val clouds: Int? = null,

    @field:SerializedName("feels_like")
    val feelsLike: Double? = null,

    @field:SerializedName("wind_gust")
    val windGust: Double? = null,

    @field:SerializedName("dt")
    val dt: Int? = null,

    @field:SerializedName("wind_deg")
    val windDeg: Int? = null,

    @field:SerializedName("dew_point")
    val dewPoint: Double? = null,

    @field:SerializedName("sunset")
    val sunset: Int? = null,

    @field:SerializedName("weather")
    val weather: List<WeatherItem?>? = null,

    @field:SerializedName("humidity")
    val humidity: Int? = null,

    @field:SerializedName("wind_speed")
    val windSpeed: Double? = null
) {
    override fun toString(): String {
        return "Current(sunrise=$sunrise\ntemp=$temp\nvisibility=$visibility\nuvi=$uvi\npressure=$pressure\nclouds=$clouds\nfeelsLike=$feelsLike\nwindGust=$windGust\ndt=$dt\nwindDeg=$windDeg\ndewPoint=$dewPoint\nsunset=$sunset\nweather=$weather\nhumidity=$humidity\nwindSpeed=$windSpeed)"
    }
}