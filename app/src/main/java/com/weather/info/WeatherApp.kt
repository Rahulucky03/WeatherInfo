package com.weather.info

import android.app.Application
import androidx.databinding.DataBindingUtil
import com.weather.info.binding.BindingComponent
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class WeatherApp : Application() {

    companion object {
        lateinit var instance: WeatherApp
    }

    fun getInstance(): WeatherApp {
        return instance
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        DataBindingUtil.setDefaultComponent(BindingComponent())

    }
}
