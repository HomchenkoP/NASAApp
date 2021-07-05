package ru.geekbrains.nasaapp.repository.retrofit

import ru.geekbrains.nasaapp.model.ApodResponseDTO

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NasaApodAPI {

    @GET("planetary/apod")
    fun getPictureOfTheDay(
        @Query("api_key") apiKey: String
    ): Call<ApodResponseDTO>

    @GET("planetary/apod")
    fun getPictureOfTheDay(
        @Query("api_key") apiKey: String,
        @Query("date") date: String
    ): Call<ApodResponseDTO>
}