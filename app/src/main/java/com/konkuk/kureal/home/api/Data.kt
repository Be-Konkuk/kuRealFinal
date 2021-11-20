package com.konkuk.kureal.home.api

data class Data(
    val article: String,
    val building: Int,
    val date: String,
    val latitude: Double,
    val longitude: Double,
    val nickname: String,
    val photo: String,
    val pk: Int,
    val tag: String
)