package com.example.sfweather.repositories

import com.example.sfweather.databases.SearchHistoryDAO
import com.example.sfweather.models.OWResult
import com.example.sfweather.models.SearchHistory
import com.example.sfweather.services.OWService
import org.koin.core.KoinComponent
import org.koin.core.inject
import retrofit2.Response

class WeatherRepository():KoinComponent {
    private val owService: OWService by inject()
    private val searchHistorydDao: SearchHistoryDAO by inject()

    suspend fun findWeatherByCityName(cityName: String): Response<OWResult> {
        return this.owService.findByCityName(cityName)
    }

    suspend fun findWeatherByCityId(cityId: Int): Response<OWResult> {
        return this.owService.findByCityId(cityId)
    }

    suspend fun getAllHistories():List<SearchHistory> {
        return searchHistorydDao.getAll()
    }

    suspend fun getLatestHistory(): SearchHistory {
        return searchHistorydDao.getLatest()
    }

    suspend fun insertHistory(searchHistory: SearchHistory) {
        val count = searchHistorydDao.getCountByCityId(searchHistory.cityId)

        if (count == 0) {
            searchHistorydDao.insert(searchHistory)
        } else {
            searchHistorydDao.update(searchHistory)
        }
    }

    suspend fun deleteHistoryByCityId(cityId: Int):Int {
        return searchHistorydDao.deleteByCityId(cityId)
    }
}