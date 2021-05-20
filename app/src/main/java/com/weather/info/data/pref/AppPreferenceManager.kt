package com.weather.info.data.pref

import android.content.Context
import com.google.gson.Gson
import com.weather.info.data.model.user.UserDto
import javax.inject.Inject

class AppPreferenceManager @Inject constructor(
    private val context: Context,
    @PreferenceInfo prefFileName: String
) : Preferences(), PreferenceSource {

    init {
        init(context, prefFileName)
    }

    //var masterData by stringPref("masterData")
    var firstTime by booleanPref("firstTime")

    var loginTokenString by stringPref("loginToken")
    var userDto by stringPref("userDto")

    override fun logout() {
        loginTokenString = null
        userDto = null
    }

    override fun saveLoginToken(loginToken: String?) {
        this.loginTokenString = loginToken
    }

    override fun getLoginToken(): String? {
        return loginTokenString
    }

    override fun getUserData(): UserDto? {
        return Gson().fromJson(userDto, UserDto::class.java)
    }

    override fun saveUserData(userDto: UserDto) {
        this.userDto = Gson().toJson(userDto)
    }
}



