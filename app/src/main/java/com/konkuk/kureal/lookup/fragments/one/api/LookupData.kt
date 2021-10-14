package com.konkuk.kureal.lookup.fragments.one.api

data class LookupData(
    val article: String,
    val date: String,
    val latitude: Double,
    val longitude: Double,
    val nickname: String,
    val photo: String,
    val pk: Int,
    val tag: String
)