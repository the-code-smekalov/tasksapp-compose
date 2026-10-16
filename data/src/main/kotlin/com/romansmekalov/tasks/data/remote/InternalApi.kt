package com.romansmekalov.tasks.data.remote

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

interface InternalApi {
    @Streaming
    @GET
    suspend fun download(@Url url: String): ResponseBody
}