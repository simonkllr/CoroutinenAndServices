package com.example.jetpackcompose.api

import android.util.Log
import com.example.jetpackcompose.data.ForecastData
import com.example.jetpackcompose.data.WeatherData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

object WeatherApiService {
    private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"

    private val client = OkHttpClient.Builder().build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(WeatherApi::class.java)

    interface WeatherApi {
        @GET("weather")
        suspend fun fetchWeather(
            @Query("q") city: String,
            @Query("appid") apiKey: String,
            @Query("units") units: String = "metric"
        ): retrofit2.Response<WeatherData>

        @GET("forecast")
        suspend fun fetchForecast(
            @Query("q") city: String,
            @Query("appid") apiKey: String,
            @Query("units") units: String = "metric"
        ): retrofit2.Response<ForecastData>
    }

    suspend fun fetchWeather(city: String, apiKey: String): WeatherData? {
        return try {
            withContext(Dispatchers.Default) {
                val response = api.fetchWeather(city, apiKey)
                if (response.isSuccessful) {
                    response.body()
                } else {
                    Log.e("WeatherApiService", "Failed to fetch data: ${response.code()}")
                    null
                }
            }
        } catch (e: Exception) {
            Log.e("WeatherApiService", "Error fetching data: ${e.message}")
            null
        }
    }

    /**
     * Fetches weather forecast data for a given city.
     *
     * @param city The name of the city for which the forecast is to be fetched.
     * @param apiKey The API key required for accessing the OpenWeather API.
     * @return An instance of [ForecastData] containing the forecast details if the API call is successful, or `null` if an error occurs.
     *
     * This method uses a suspend function to perform the network operation asynchronously on the IO dispatcher.
     * It handles potential exceptions and logs errors if the forecast cannot be retrieved.
     */
    suspend fun fetchForecast(city: String, apiKey: String): ForecastData? {
        return try {
            withContext(Dispatchers.IO) { // Dispatcher.IO für Netzwerkoperationen
                val response = api.fetchForecast(city, apiKey)
                if (response.isSuccessful) {
                    response.body()
                } else {
                    Log.e("WeatherApiService", "Failed to fetch forecast: ${response.code()}")
                    null
                }
            }
        } catch (e: Exception) {
            Log.e("WeatherApiService", "Error fetching forecast: ${e.message}")
            null
        }
    }

}
