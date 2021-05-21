package com.weather.info.data

import android.content.Context
import com.weather.info.data.model.user.UserDto
import com.weather.info.data.model.weather.Weather
import com.weather.info.data.pref.AppPreferenceManager
import com.weather.info.data.remote.RemoteDataManager
import com.weather.info.data.room.RoomDataManager
import com.weather.info.data.room.entity.History
import io.reactivex.Observable
import retrofit2.Response
import javax.inject.Inject

class AppDataManager @Inject constructor(
    context: Context,
    val remoteDataManager: RemoteDataManager,
    private var localDataManager: RoomDataManager,
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

    override suspend fun saveHistory(history: History) = localDataManager.saveHistory(history)

    override suspend fun getAllHistory(): List<History> = localDataManager.getAllHistory()

    override suspend fun getHistoryByID(id: Int): History = localDataManager.getHistoryByID(id)

    override suspend fun deleteHistory(history: History): Int =
        localDataManager.deleteHistory(history)
}