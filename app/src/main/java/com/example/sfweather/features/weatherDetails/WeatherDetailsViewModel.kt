package com.example.sfweather.features.weatherDetails

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sfweather.models.OWApiError
import com.example.sfweather.models.OWResult
import com.example.sfweather.models.SearchHistory
import com.example.sfweather.repositories.WeatherRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject
import retrofit2.Response

class WeatherDetailsViewModel: ViewModel(), KoinComponent {
    private val weatherRepository: WeatherRepository by inject()

    val weatherDetails: MutableLiveData<WeatherDetailsData> = MutableLiveData()
    val isLoading: MutableLiveData<Boolean> = MutableLiveData()
    val errorMessage: MutableLiveData<String> = MutableLiveData()

    //region public methods
    fun fetchLastStoredWeather() {
        CoroutineScope(Dispatchers.IO).launch {
            val searchHistory = weatherRepository.getLatestHistory()

            if (searchHistory != null) {
                fetchWeatherByCityId(searchHistory.cityId)
            }
        }
    }

    fun fetchWeatherByCityName(cityName: String) {
        CoroutineScope(Dispatchers.IO).launch {
            isLoading.postValue(true)

            val response = weatherRepository.findWeatherByCityName(cityName)

            handleResponse(response)
        }
    }

    fun fetchWeatherByCityId(cityId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            isLoading.postValue(true)

            val response = weatherRepository.findWeatherByCityId(cityId)

            handleResponse(response)
        }
    }
    //endregion

    //region private methods
    private fun handleResponse(response: Response<OWResult>) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                if (response.isSuccessful) {
                    val owResult = response.body()!!

                    weatherDetails.postValue(WeatherDetailsData(owResult))

                    // store to db
                    insertSearchHistory(owResult)
                } else {
                    val apiError = OWApiError.createFromResponse(response.errorBody()!!)

                    errorMessage.postValue(apiError.message)
                }
            } catch (e: Exception) {
                e.message?.let {
                    errorMessage.postValue(it)
                } ?: run {
                    errorMessage.postValue("Unknown Error")
                }
            } finally {
                isLoading.postValue(false)
            }
        }
    }

    private fun insertSearchHistory(owResult: OWResult) {
        val timestamp = System.currentTimeMillis() / 1000;
        val searchHistory = SearchHistory(owResult.id, owResult.name, timestamp)

        CoroutineScope(Dispatchers.IO).launch {
            weatherRepository.insertHistory(searchHistory)
        }
    }
    //endregion
}