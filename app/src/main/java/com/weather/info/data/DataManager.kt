package com.weather.info.data

import com.weather.info.data.pref.PreferenceSource
import com.weather.info.data.remote.RemoteSource

interface DataManager : RemoteSource, PreferenceSource/*, DatabaseSource*/ {
    enum class LoggedInMode(val type: Int) {
        LOGGED_IN_MODE_LOGGED_OUT(0),
        LOGGED_IN_MODE_LOGGEDIN(1)
    }

    enum class LoggedInType(val type: Int) {
        LOGGING_IN_FIRST_TIME(0),
        LOGGING_IN_REGULAR(1)
    }
}