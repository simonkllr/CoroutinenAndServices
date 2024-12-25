package com.example.jetpackcompose.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpackcompose.api.WeatherApiService
import com.example.jetpackcompose.data.ForecastItem
import com.example.jetpackcompose.data.WeatherData
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class WeatherViewModel : ViewModel() {

    private val _currentWeather = MutableStateFlow<WeatherData?>(null)
    val currentWeather: StateFlow<WeatherData?> = _currentWeather

    private val _forecast = MutableStateFlow<List<ForecastItem>>(emptyList())
    val forecast: StateFlow<List<ForecastItem>> = _forecast

    private val _iconUrl = MutableStateFlow<String?>(null)
    val iconUrl: StateFlow<String?> get() = _iconUrl

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage

    fun fetchWeatherData(city: String, apiKey: String) {
        viewModelScope.launch {
            try {
                val weatherResponse = WeatherApiService.fetchWeather(city, apiKey)
                if (weatherResponse != null) {
                    _currentWeather.value = weatherResponse
                    fetchWeatherIcon(weatherResponse.weather.firstOrNull()?.icon.orEmpty())
                    _errorMessage.value = null
                } else {
                    _errorMessage.value = "Failed to fetch weather. Please check your API key or city name."
                }
            } catch (e: Exception) {
                _errorMessage.value = "An error occurred: ${e.localizedMessage}"
            }
        }
    }
    /**
     * Fetches the weather forecast data for a specified city and updates the ViewModel state.
     *
     * @param city The name of the city for which the forecast data is to be retrieved.
     * @param apiKey The API key required to access the OpenWeather API.
     *
     * This method launches a coroutine in the ViewModel's scope to execute the API call asynchronously.
     * - If the API call is successful, it updates the `_forecast` variable with the forecast data and clears any error messages.
     * - If the API call fails or an exception occurs, it sets an appropriate error message in `_errorMessage`.
     */
    fun fetchForecastData(city: String, apiKey: String) {
        viewModelScope.launch {
            try {
                val forecastResponse = WeatherApiService.fetchForecast(city, apiKey)
                if (forecastResponse != null) {
                    _forecast.value = forecastResponse.list // Speichert die Vorhersagedaten
                    _errorMessage.value = null // Fehlermeldung zurücksetzen
                } else {
                    _errorMessage.value = "Failed to fetch forecast data. Please check your API key or city name."
                }
            } catch (e: Exception) {
                _errorMessage.value = "An error occurred: ${e.localizedMessage}"
            }
        }
    }


    private fun fetchWeatherIcon(iconId: String) {
        if (iconId.isNotEmpty()) {
            _iconUrl.value = "https://openweathermap.org/img/wn/$iconId@2x.png"
        }
    }
}
