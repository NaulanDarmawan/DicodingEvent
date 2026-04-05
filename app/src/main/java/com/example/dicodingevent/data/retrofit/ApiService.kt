package com.example.dicodingevent.data.retrofit

import com.example.dicodingevent.data.response.DetailEventResponse
import com.example.dicodingevent.data.response.EventResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    // Endpoints menggunakan Coroutines (suspend) agar asinkron & lebih ringan
    @GET("events")
    suspend fun getEvents(
        @Query("active") active: Int,
        @Query("q") keyword: String? = null
    ): EventResponse

    @GET("events/{id}")
    suspend fun getDetailEvent(
        @Path("id") id: String
    ): DetailEventResponse
}