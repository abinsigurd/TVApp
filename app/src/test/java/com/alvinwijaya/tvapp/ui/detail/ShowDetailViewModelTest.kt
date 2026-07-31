package com.alvinwijaya.tvapp.ui.detail

import com.alvinwijaya.tvapp.MainDispatcherRule
import com.alvinwijaya.tvapp.data.model.CastCredit
import com.alvinwijaya.tvapp.data.model.Character as ShowCharacter
import com.alvinwijaya.tvapp.data.model.Person
import com.alvinwijaya.tvapp.data.model.Rating
import com.alvinwijaya.tvapp.data.model.Show
import com.alvinwijaya.tvapp.data.model.ShowDetailContent
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
    fun `loadShow requests correct show id and returns content`() = runTest {
        val expectedContent = createDetailContent()

        val repository = FakeDetailShowRepository(
            detailContent = expectedContent
        )

        val viewModel = ShowDetailViewModel(
            repository = repository,
            showId = TEST_SHOW_ID
        )

        assertEquals(
            ShowDetailUiState.Loading,
            viewModel.uiState.value
        )

        advanceUntilIdle()

        assertEquals(
            TEST_SHOW_ID,
            repository.lastRequestedShowId
        )

        assertEquals(
            1,
            repository.getShowDetailCallCount
        )

        assertEquals(
            ShowDetailUiState.Success(expectedContent),
            viewModel.uiState.value
        )
    }

    private fun createDetailContent(): ShowDetailContent {
        val show = Show(
            id = TEST_SHOW_ID,
            name = "Test Show",
            url = "https://www.tvmaze.com/shows/$TEST_SHOW_ID",
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

        val cast = listOf(
            CastCredit(
                person = Person(
                    id = 100,
                    name = "Test Actor",
                    image = ShowImage(
                        medium = "https://example.com/person.jpg"
                    )
                ),
                character = ShowCharacter(
                    id = 200,
                    name = "Test Character"
                )
            )
        )

        return ShowDetailContent(
            show = show,
            cast = cast
        )
    }

    private companion object {
        const val TEST_SHOW_ID = 42
    }
}

private class FakeDetailShowRepository(
    private val detailContent: ShowDetailContent
) : ShowRepository {

    var lastRequestedShowId: Int? = null
        private set

    var getShowDetailCallCount: Int = 0
        private set

    override suspend fun getShows(
        page: Int
    ): List<Show> {
        error(
            "getShows is not used in ShowDetailViewModel tests"
        )
    }

    override suspend fun getShowDetailContent(
        showId: Int
    ): ShowDetailContent {
        lastRequestedShowId = showId
        getShowDetailCallCount += 1

        return detailContent
    }
}