package com.alvinwijaya.tvapp.ui.list

import com.alvinwijaya.tvapp.MainDispatcherRule
import com.alvinwijaya.tvapp.data.model.Show
import com.alvinwijaya.tvapp.data.model.ShowDetailContent
import com.alvinwijaya.tvapp.data.model.ShowPage
import com.alvinwijaya.tvapp.data.repository.ShowRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ShowListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `loadShows requests first page and returns shows`() = runTest {
        val firstPageShows = listOf(
            createShow(
                id = 1,
                name = "First Show"
            ),
            createShow(
                id = 2,
                name = "Second Show"
            )
        )

        val repository = FakeShowRepository().apply {
            pages[0] = ShowPage(
                shows = firstPageShows,
                endReached = false
            )
        }

        val viewModel = ShowListViewModel(
            repository = repository
        )

        assertEquals(
            ShowListUiState.Loading,
            viewModel.uiState.value
        )

        advanceUntilIdle()

        assertEquals(
            listOf(0),
            repository.requestedPages
        )

        assertEquals(
            ShowListUiState.Success(
                shows = firstPageShows
            ),
            viewModel.uiState.value
        )
    }

    @Test
    fun `loadShows returns error when first page fails`() = runTest {
        val repository = FakeShowRepository().apply {
            errors[0] = IllegalStateException(
                "Network unavailable"
            )
        }

        val viewModel = ShowListViewModel(
            repository = repository
        )

        advanceUntilIdle()

        assertEquals(
            ShowListUiState.Error(
                message = "Network unavailable"
            ),
            viewModel.uiState.value
        )
    }

    @Test
    fun `loadShows retries after initial failure`() = runTest {
        val expectedShows = listOf(
            createShow(
                id = 1,
                name = "Recovered Show"
            )
        )

        val repository = FakeShowRepository().apply {
            errors[0] = IllegalStateException(
                "Network unavailable"
            )
        }

        val viewModel = ShowListViewModel(
            repository = repository
        )

        advanceUntilIdle()

        repository.errors.remove(0)
        repository.pages[0] = ShowPage(
            shows = expectedShows,
            endReached = false
        )

        viewModel.loadShows()
        advanceUntilIdle()

        assertEquals(
            listOf(0, 0),
            repository.requestedPages
        )

        assertEquals(
            ShowListUiState.Success(
                shows = expectedShows
            ),
            viewModel.uiState.value
        )
    }

    @Test
    fun `loadNextPage appends shows from next page`() = runTest {
        val firstShow = createShow(
            id = 1,
            name = "First Show"
        )

        val secondShow = createShow(
            id = 251,
            name = "Next Page Show"
        )

        val repository = FakeShowRepository().apply {
            pages[0] = ShowPage(
                shows = listOf(firstShow),
                endReached = false
            )

            pages[1] = ShowPage(
                shows = listOf(secondShow),
                endReached = false
            )
        }

        val viewModel = ShowListViewModel(
            repository = repository
        )

        advanceUntilIdle()

        viewModel.loadNextPage()
        advanceUntilIdle()

        val currentState =
            viewModel.uiState.value as ShowListUiState.Success

        assertEquals(
            listOf(0, 1),
            repository.requestedPages
        )

        assertEquals(
            listOf(firstShow, secondShow),
            currentState.shows
        )

        assertFalse(currentState.isLoadingMore)
        assertFalse(currentState.endReached)
        assertEquals(null, currentState.loadMoreError)
    }

    @Test
    fun `loadNextPage keeps current shows when request fails`() = runTest {
        val firstShow = createShow(
            id = 1,
            name = "First Show"
        )

        val repository = FakeShowRepository().apply {
            pages[0] = ShowPage(
                shows = listOf(firstShow),
                endReached = false
            )

            errors[1] = IllegalStateException(
                "Unable to load next page"
            )
        }

        val viewModel = ShowListViewModel(
            repository = repository
        )

        advanceUntilIdle()

        viewModel.loadNextPage()
        advanceUntilIdle()

        val errorState =
            viewModel.uiState.value as ShowListUiState.Success

        assertEquals(
            listOf(firstShow),
            errorState.shows
        )

        assertEquals(
            "Unable to load next page",
            errorState.loadMoreError
        )

        assertFalse(errorState.isLoadingMore)
        assertFalse(errorState.endReached)

        repository.errors.remove(1)
        repository.pages[1] = ShowPage(
            shows = listOf(
                createShow(
                    id = 251,
                    name = "Recovered Page Show"
                )
            ),
            endReached = false
        )

        viewModel.loadNextPage()
        advanceUntilIdle()

        val recoveredState =
            viewModel.uiState.value as ShowListUiState.Success

        assertEquals(
            listOf(0, 1, 1),
            repository.requestedPages
        )

        assertEquals(
            2,
            recoveredState.shows.size
        )

        assertEquals(
            null,
            recoveredState.loadMoreError
        )
    }

    @Test
    fun `loadNextPage marks end and prevents another request`() = runTest {
        val firstShow = createShow(
            id = 1,
            name = "First Show"
        )

        val repository = FakeShowRepository().apply {
            pages[0] = ShowPage(
                shows = listOf(firstShow),
                endReached = false
            )

            pages[1] = ShowPage(
                shows = emptyList(),
                endReached = true
            )
        }

        val viewModel = ShowListViewModel(
            repository = repository
        )

        advanceUntilIdle()

        viewModel.loadNextPage()
        advanceUntilIdle()

        val currentState =
            viewModel.uiState.value as ShowListUiState.Success

        assertTrue(currentState.endReached)
        assertFalse(currentState.isLoadingMore)

        viewModel.loadNextPage()
        advanceUntilIdle()

        assertEquals(
            listOf(0, 1),
            repository.requestedPages
        )
    }

    private fun createShow(
        id: Int,
        name: String
    ): Show {
        return Show(
            id = id,
            name = name
        )
    }
}

private class FakeShowRepository : ShowRepository {

    val pages = mutableMapOf<Int, ShowPage>()
    val errors = mutableMapOf<Int, Throwable>()
    val requestedPages = mutableListOf<Int>()

    override suspend fun getShows(
        page: Int
    ): ShowPage {
        requestedPages += page

        errors[page]?.let { error ->
            throw error
        }

        return pages[page] ?: ShowPage(
            shows = emptyList(),
            endReached = true
        )
    }

    override suspend fun getShowDetailContent(
        showId: Int
    ): ShowDetailContent {
        error(
            "getShowDetailContent is not used in ShowListViewModel tests"
        )
    }
}