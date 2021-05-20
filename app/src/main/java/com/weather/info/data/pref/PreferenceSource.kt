package com.weather.info.data.pref

import com.weather.info.data.model.user.UserDto

interface PreferenceSource {

    fun logout()

    fun saveLoginToken(loginToken: String?)
    fun getLoginToken(): String?

    fun saveUserData(userDto: UserDto)
    fun getUserData(): UserDto?

}