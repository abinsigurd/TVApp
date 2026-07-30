package com.alvinwijaya.tvapp.data.repository

import com.alvinwijaya.tvapp.data.model.Show
import com.alvinwijaya.tvapp.data.remote.TvMazeApi

interface ShowRepository {

    suspend fun getShows(page: Int = 0): List<Show>

    suspend fun getShowDetail(showId: Int): Show
}

class ShowRepositoryImpl(
    private val api: TvMazeApi
) : ShowRepository {

    override suspend fun getShows(page: Int): List<Show> {
        return api.getShows(page)
    }

    override suspend fun getShowDetail(showId: Int): Show {
        return api.getShowDetail(showId)
    }
}