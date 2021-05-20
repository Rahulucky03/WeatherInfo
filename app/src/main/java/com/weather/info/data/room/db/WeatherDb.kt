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

package com.weather.info.data.room.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.weather.info.data.room.dao.HistoryDao
import com.weather.info.data.room.entity.History

/**
 * Main database description.
 */
@Database(
    entities = [
        History::class/*,
        Repo::class*/],
    version = 1,
    exportSchema = false
)
abstract class WeatherDb : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
}
