package com.example.sfweather.services

import com.example.sfweather.BuildConfig
import com.example.sfweather.models.OWResult
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.Response
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface OWService {

    @GET("weather")
    suspend fun findByCityName(@Query("q") cityName: String): Response<OWResult>

    @GET("weather")
    suspend fun findByCityId(@Query("id") cityId: Int): Response<OWResult>

    companion object Factory {
        fun create(): OWService {
            val interceptor = object: Interceptor {
                override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
                    var request = chain.request()

                    val newUrl: HttpUrl = request.url.newBuilder().addQueryParameter("appId", BuildConfig.OPENWEATHER_API_TOKEN).build()

                    request = request.newBuilder()
                        .url(newUrl)
                        .build()

                    return chain.proceed(request)
                }
            }

            val okHttpClient = OkHttpClient().newBuilder().
                addInterceptor(interceptor)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BuildConfig.OPENWEATHER_API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()

            return retrofit.create(OWService::class.java);
        }
    }
}