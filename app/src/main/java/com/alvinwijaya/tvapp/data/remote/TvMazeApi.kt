package com.alvinwijaya.tvapp.data.remote

import com.alvinwijaya.tvapp.data.model.Show
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TvMazeApi {

    @GET("shows")
    suspend fun getShows(
        @Query("page") page: Int = 0
    ): List<Show>

    @GET("shows/{id}")
    suspend fun getShowDetail(
        @Path("id") showId: Int,
        @Query("embed") embed: String = "cast"
    ): Show
}