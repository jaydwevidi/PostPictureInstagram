package com.example.uploadfeed.retrofit

data class UploadRequestBody(
    val Image: String,
    val description: String = "jay sample description bla bla bla",
    val hashtag: List<String> = listOf<String>("#jay"),
    val user_id: Int
)