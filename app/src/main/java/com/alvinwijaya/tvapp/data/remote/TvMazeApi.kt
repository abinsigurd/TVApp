package com.alvinwijaya.tvapp.data.remote

import com.alvinwijaya.tvapp.data.model.Season
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
        @Query("embed[]")
        embeds: List<String> = listOf(
            "cast",
            "episodes"
        )
    ): Show

    @GET("shows/{id}/seasons")
    suspend fun getShowSeasons(
        @Path("id") showId: Int
    ): List<Season>
}