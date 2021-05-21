package com.weather.info.data.room

import com.weather.info.data.room.entity.History

interface RoomManager {
    suspend fun saveHistory(history: History)
    suspend fun getAllHistory(): List<History>
    suspend fun getHistoryByID(id: Int): History
    suspend fun deleteHistory(history: History): Int
}