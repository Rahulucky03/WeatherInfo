package com.weather.info.data.room

import android.content.Context
import com.weather.info.data.room.db.WeatherDb
import com.weather.info.data.room.entity.History
import javax.inject.Inject

class RoomDataManager @Inject constructor(val context: Context, val database: WeatherDb) :
    RoomManager {

    override suspend fun saveHistory(history: History) {
        database.historyDao().insertHistory(history)
    }

    override suspend fun getAllHistory(): List<History> {
        return database.historyDao().getAllHistory()
    }

    override suspend fun getHistoryByID(id: Int): History {
        return database.historyDao().findByHistoryId(id)
    }

    override suspend fun deleteHistory(history: History): Int {
        return database.historyDao().deleteHistory(history)
    }

}