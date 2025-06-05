package com.zoomvsdkkotlin.utils

import com.google.gson.JsonObject
import com.zoomvsdkkotlin.activities.JWTOptions
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    @Headers("Accept: application/json")
    @POST("zoomtoken")
    fun getJWT(@Query ("topic") topic: String,
               @Query ("name")  name: String,
               @Query ("password") password: String,
               @Body body: JWTOptions
    ): Call<JsonObject>
}