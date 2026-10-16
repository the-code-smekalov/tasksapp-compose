package com.romansmekalov.tasks.data.remote

import com.romansmekalov.tasks.data.remote.dto.CatImageDto
import retrofit2.http.GET

interface CatApi {
    @GET("v1/images/search")
    suspend fun randomImage(): List<CatImageDto>
}
