package com.alvinwijaya.tvapp.data.repository

import com.alvinwijaya.tvapp.data.model.ShowDetailContent
import com.alvinwijaya.tvapp.data.model.ShowPage
import com.alvinwijaya.tvapp.data.remote.TvMazeApi
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import retrofit2.HttpException

interface ShowRepository {

    suspend fun getShows(
        page: Int = 0
    ): ShowPage

    suspend fun getShowDetailContent(
        showId: Int
    ): ShowDetailContent
}

class ShowRepositoryImpl(
    private val api: TvMazeApi
) : ShowRepository {

    override suspend fun getShows(
        page: Int
    ): ShowPage {
        return try {
            ShowPage(
                shows = api.getShows(page),
                endReached = false
            )
        } catch (exception: HttpException) {
            if (exception.code() == 404) {
                ShowPage(
                    shows = emptyList(),
                    endReached = true
                )
            } else {
                throw exception
            }
        }
    }

    override suspend fun getShowDetailContent(
        showId: Int
    ): ShowDetailContent = coroutineScope {
        val showDeferred = async {
            api.getShowDetail(showId)
        }

        val seasonsDeferred = async {
            try {
                api.getShowSeasons(showId)
            } catch (exception: CancellationException) {
                throw exception
            } catch (_: Exception) {
                emptyList()
            }
        }

        val show = showDeferred.await()
        val seasons = seasonsDeferred.await()

        ShowDetailContent(
            show = show,
            cast = show.embedded?.cast.orEmpty(),
            seasons = seasons,
            episodes = show.embedded?.episodes.orEmpty()
        )
    }
}