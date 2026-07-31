package com.alvinwijaya.tvapp.ui.detail

import com.alvinwijaya.tvapp.MainDispatcherRule
import com.alvinwijaya.tvapp.data.model.Rating
import com.alvinwijaya.tvapp.data.model.Show
import com.alvinwijaya.tvapp.data.model.ShowImage
import com.alvinwijaya.tvapp.data.repository.ShowRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ShowDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `loadShow requests the correct show id and returns success`() = runTest {
        val requestedShowId = 42

        val expectedShow = Show(
            id = requestedShowId,
            name = "Test Show",
            url = "https://www.tvmaze.com/shows/$requestedShowId",
            summary = "<p>Test summary</p>",
            premiered = "2024-01-01",
            rating = Rating(
                average = 8.5
            ),
            image = ShowImage(
                medium = "https://example.com/medium.jpg",
                original = "https://example.com/original.jpg"
            )
        )

        val repository = FakeDetailShowRepository(
            show = expectedShow
        )

        val viewModel = ShowDetailViewModel(
            repository = repository,
            showId = requestedShowId
        )

        assertEquals(
            ShowDetailUiState.Loading,
            viewModel.uiState.value
        )

        advanceUntilIdle()

        assertEquals(
            requestedShowId,
            repository.lastRequestedShowId
        )

        assertEquals(
            ShowDetailUiState.Success(expectedShow),
            viewModel.uiState.value
        )
    }
}

private class FakeDetailShowRepository(
    private val show: Show
) : ShowRepository {

    var lastRequestedShowId: Int? = null
        private set

    override suspend fun getShows(
        page: Int
    ): List<Show> {
        error(
            "getShows is not used in ShowDetailViewModel tests"
        )
    }

    override suspend fun getShowDetail(
        showId: Int
    ): Show {
        lastRequestedShowId = showId
        return show
    }
}