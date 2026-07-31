package com.alvinwijaya.tvapp.data.repository

import com.alvinwijaya.tvapp.data.model.Show
import com.alvinwijaya.tvapp.data.model.ShowDetailContent
import com.alvinwijaya.tvapp.data.remote.TvMazeApi
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

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
    ): ShowDetailContent = coroutineScope {
        val showDeferred = async {
            api.getShowDetail(showId)
        }

        val seasonsDeferred = async {
            try {
                api.getShowSeasons(showId)
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
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