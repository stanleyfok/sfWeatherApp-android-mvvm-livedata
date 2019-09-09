package com.example.sfweather.features.weatherHistory

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sfweather.models.SearchHistory
import com.example.sfweather.repositories.WeatherRepository
import kotlinx.coroutines.*
import org.koin.core.KoinComponent
import org.koin.core.inject

class WeatherHistoryViewModel: ViewModel(), KoinComponent {
    private val weatherRepository: WeatherRepository by inject()

    val searchHistories: MutableLiveData<List<SearchHistory>> = MutableLiveData()
    val selectedSearchHistory: MutableLiveData<SearchHistory> = MutableLiveData()
    var isEdit: MutableLiveData<Boolean> = MutableLiveData(false)

    //region public methods
    fun loadSearchHistories() {
        CoroutineScope(Dispatchers.IO).launch {
            searchHistories.postValue(weatherRepository.getAllHistories())
        }
    }

    fun getSearchHistoryAtPosition(position: Int): SearchHistory? {
        this.searchHistories.value?.let {
            if (position < it.size) {
                return it[position]
            }
        }

        return null
    }

    fun getSearchHistoryCount():Int {
        this.searchHistories.value?.let {
            return it.size
        }

        return 0
    }

    fun selectSearchHistoryAtPosition(position: Int) {
        val searchHistory = this.getSearchHistoryAtPosition(position)

        if (searchHistory != null) {
            selectedSearchHistory.postValue(searchHistory)
        }
    }

    fun removeSearchHistoryAtPosition(position: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val searchHistory = getSearchHistoryAtPosition(position)

            if (searchHistory != null) {
                val deleteCount = weatherRepository.deleteHistoryByCityId(searchHistory.cityId)

                if (deleteCount > 0) {
                    val updatedSearchHistories = searchHistories.value!!.filter {
                        it.cityId != searchHistory.cityId
                    }

                    searchHistories.postValue(updatedSearchHistories)
                }
            }
        }
    }

    fun toggleIsEdit() {
        isEdit.postValue(!isEdit.value!!)
    }
    //endregion
}