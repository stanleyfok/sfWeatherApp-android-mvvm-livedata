package com.example.sfweather.databases

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.sfweather.models.SearchHistory

@Dao
interface SearchHistoryDAO {
    @Query("Select * From searchHistory ORDER BY timestamp DESC")
    suspend fun getAll(): List<SearchHistory>

    @Query("Select * From searchHistory ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatest(): SearchHistory

    @Query("Select count(cityId) From searchHistory WHERE cityId = :cityId")
    suspend fun getCountByCityId(cityId: Int):Int

    @Insert
    suspend fun insert(vararg searchHistory: SearchHistory)

    @Update
    suspend fun update(vararg searchHistory: SearchHistory)

    @Query("DELETE FROM searchHistory WHERE cityId = :cityId")
    suspend fun deleteByCityId(cityId: Int):Int
}