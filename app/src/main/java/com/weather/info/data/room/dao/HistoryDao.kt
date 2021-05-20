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

package com.weather.info.data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.weather.info.data.room.entity.History

/**
 * Interface for database access for User related operations.
 */
@Dao
interface HistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(history: History)

    @Query("SELECT * FROM history WHERE historyId = :historyId")
    fun findByHistoryId(historyId: Int): LiveData<History>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHistory(history: History)


    /*abstract fun loadUsers(user: User): LiveData<User?>*/
}
