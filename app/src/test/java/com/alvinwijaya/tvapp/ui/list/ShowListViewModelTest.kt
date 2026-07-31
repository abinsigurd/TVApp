package com.alvinwijaya.tvapp.ui.list

import com.alvinwijaya.tvapp.MainDispatcherRule
import com.alvinwijaya.tvapp.data.model.Rating
import com.alvinwijaya.tvapp.data.model.Show
import com.alvinwijaya.tvapp.data.model.ShowImage
import com.alvinwijaya.tvapp.data.repository.ShowRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class ShowListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `loadShows returns success when repository succeeds`() = runTest {
        val expectedShows = listOf(
            createShow(
                id = 1,
                name = "Under the Dome",
                rating = 6.5
            ),
            createShow(
                id = 2,
                name = "Person of Interest",
                rating = 8.8
            )
        )

        val repository = FakeShowRepository(
            showsResult = Result.success(expectedShows)
        )

        val viewModel = ShowListViewModel(
            repository = repository
        )

        assertEquals(
            ShowListUiState.Loading,
            viewModel.uiState.value
        )

        advanceUntilIdle()

        assertEquals(
            ShowListUiState.Success(expectedShows),
            viewModel.uiState.value
        )

        assertEquals(
            1,
            repository.getShowsCallCount
        )
    }

    @Test
    fun `loadShows returns error when repository fails`() = runTest {
        val repository = FakeShowRepository(
            showsResult = Result.failure(
                IOException("No internet connection")
            )
        )

        val viewModel = ShowListViewModel(
            repository = repository
        )

        advanceUntilIdle()

        val currentState = viewModel.uiState.value

        assertTrue(
            currentState is ShowListUiState.Error
        )

        assertEquals(
            "No internet connection",
            (currentState as ShowListUiState.Error).message
        )

        assertEquals(
            1,
            repository.getShowsCallCount
        )
    }

    @Test
    fun `retry loads shows again after an error`() = runTest {
        val repository = FakeShowRepository(
            showsResult = Result.failure(
                IOException("Temporary error")
            )
        )

        val viewModel = ShowListViewModel(
            repository = repository
        )

        advanceUntilIdle()

        assertTrue(
            viewModel.uiState.value is ShowListUiState.Error
        )

        val expectedShows = listOf(
            createShow(
                id = 10,
                name = "The Expanse",
                rating = 8.5
            )
        )

        repository.setShowsResult(
            Result.success(expectedShows)
        )

        viewModel.loadShows()

        advanceUntilIdle()

        assertEquals(
            ShowListUiState.Success(expectedShows),
            viewModel.uiState.value
        )

        assertEquals(
            2,
            repository.getShowsCallCount
        )
    }

    private fun createShow(
        id: Int,
        name: String,
        rating: Double?
    ): Show {
        return Show(
            id = id,
            name = name,
            url = "https://www.tvmaze.com/shows/$id",
            summary = "<p>Test summary</p>",
            premiered = "2020-01-01",
            rating = Rating(
                average = rating
            ),
            image = ShowImage(
                medium = "https://example.com/medium.jpg",
                original = "https://example.com/original.jpg"
            )
        )
    }
}

private class FakeShowRepository(
    private var showsResult: Result<List<Show>>
) : ShowRepository {

    var getShowsCallCount: Int = 0
        private set

    fun setShowsResult(
        result: Result<List<Show>>
    ) {
        showsResult = result
    }

    override suspend fun getShows(
        page: Int
    ): List<Show> {
        getShowsCallCount += 1
        return showsResult.getOrThrow()
    }

    override suspend fun getShowDetail(
        showId: Int
    ): Show {
        error(
            "getShowDetail is not used in ShowListViewModel tests"
        )
    }
}