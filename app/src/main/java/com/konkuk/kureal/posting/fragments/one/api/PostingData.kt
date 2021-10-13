package com.konkuk.kureal.posting.fragments.one.api

data class PostingData(
    val date: String,
    val nickname: String,
    val article: String,
    val photo: String,
    val tag: String,
    val latitude: Double,
    val longitude: Double
)