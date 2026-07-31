package com.alvinwijaya.tvapp.data.repository

import com.alvinwijaya.tvapp.data.model.Show
import com.alvinwijaya.tvapp.data.model.ShowDetailContent
import com.alvinwijaya.tvapp.data.remote.TvMazeApi

interface ShowRepository {

    suspend fun getShows(
        page: Int = 0
    ): List<Show>

    suspend fun getShowDetailContent(
        showId: Int
    ): ShowDetailContent
}

class ShowRepositoryImpl(
    private val api: TvMazeApi
) : ShowRepository {

    override suspend fun getShows(
        page: Int
    ): List<Show> {
        return api.getShows(page)
    }

    override suspend fun getShowDetailContent(
        showId: Int
    ): ShowDetailContent {
        val show = api.getShowDetail(showId)

        return ShowDetailContent(
            show = show,
            cast = show.embedded?.cast.orEmpty()
        )
    }
}