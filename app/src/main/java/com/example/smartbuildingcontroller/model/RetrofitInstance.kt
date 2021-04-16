package com.example.smartbuildingcontroller.model

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object RetrofitInstance {
    private val retrofit by lazy{
        Retrofit.Builder().baseUrl(URI.LoginUri).addConverterFactory(GsonConverterFactory.create()).build()
    }
    val api :SimpleApi by lazy {
        retrofit.create(SimpleApi::class.java)
    }
}