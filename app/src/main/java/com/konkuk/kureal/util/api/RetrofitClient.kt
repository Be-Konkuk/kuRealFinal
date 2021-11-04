package com.konkuk.kureal.util.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    //private const val BASE_URL = "http://192.168.10.102:8080/" //지원 집 와이파이
    //private const val BASE_URL = "http://192.168.169.221:8080/" //건대 와이파이
    private const val BASE_URL = "http://192.168.0.12:8080/" //서윤 집
    //private const val BASE_URL = "http://10.0.2.2:8080" //에뮬레이터

    val getApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(OkHttpClient())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create()).build()
        .create(RetrofitService::class.java)
}