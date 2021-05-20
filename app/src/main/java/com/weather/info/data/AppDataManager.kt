package com.weather.info.data

import android.content.Context
import com.weather.info.data.model.Weather
import com.weather.info.data.model.user.UserDto
import com.weather.info.data.pref.AppPreferenceManager
import com.weather.info.data.remote.RemoteDataManager
import io.reactivex.Observable
import retrofit2.Response
import javax.inject.Inject

class AppDataManager @Inject constructor(
    context: Context,
    val remoteDataManager: RemoteDataManager,
    /*private var localDataManager: LocalDataManager,*/
    preferenceManager: AppPreferenceManager
) : DataManager {

    private var appPreferenceManager: AppPreferenceManager = preferenceManager


    override fun logout() = appPreferenceManager.logout()


    override fun saveLoginToken(loginToken: String?) =
        appPreferenceManager.saveLoginToken(loginToken)

    override fun getLoginToken(): String? = appPreferenceManager.getLoginToken()

    override fun getUserData(): UserDto? = appPreferenceManager.getUserData()
    override fun saveUserData(userDto: UserDto) = appPreferenceManager.saveUserData(userDto)


    override fun fetchWeather(lat: Double, lon: Double): Observable<Response<Weather>> {
        return remoteDataManager.fetchWeather(lat, lon)
    }

}