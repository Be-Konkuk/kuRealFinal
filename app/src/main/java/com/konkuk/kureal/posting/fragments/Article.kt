package com.konkuk.kureal.posting.fragments

data class Article(
    var pk: Int,
    var date: String,
    var nickname: String,
    var article: String,
    var photo: String,
    var tag: String,
    var latitude: Double,
    var longitude: Double
)
