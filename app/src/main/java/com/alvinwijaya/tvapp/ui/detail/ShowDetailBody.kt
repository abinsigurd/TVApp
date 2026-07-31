package com.alvinwijaya.tvapp.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alvinwijaya.tvapp.data.model.ShowDetailContent
import com.alvinwijaya.tvapp.ui.detail.components.CastSection
import com.alvinwijaya.tvapp.ui.detail.components.DetailPoster
import com.alvinwijaya.tvapp.ui.detail.components.MetadataChip
import com.alvinwijaya.tvapp.ui.detail.components.SeasonSection
import com.alvinwijaya.tvapp.ui.detail.components.episodeSection
import java.util.Locale

@Composable
internal fun ShowDetailBody(
    content: ShowDetailContent,
    selectedSeasonNumber: Int?,
    onSeasonSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val show = content.show

    val posterUrl = show.image?.original
        ?: show.image?.medium

    val ratingText = show.rating?.average?.let { rating ->
        String.format(
            Locale.getDefault(),
            "%.1f",
            rating
        )
    } ?: "N/A"

    val premiereText = show.premiered
        ?.takeIf { it.isNotBlank() }
        ?: "Not available"

    val summaryText = show.summary
        ?.toPlainText()
        ?.takeIf { it.isNotBlank() }
        ?: "No summary available."

    val seasonNumbers = (
            content.seasons.mapNotNull { season ->
                season.number
            } +
                    content.episodes.mapNotNull { episode ->
                        episode.season
                    }
            )
        .distinct()
        .sorted()

    val activeSeasonNumber = selectedSeasonNumber
        ?: seasonNumbers.firstOrNull()

    val episodeCounts = content.episodes
        .mapNotNull { episode ->
            episode.season
        }
        .groupingBy { seasonNumber ->
            seasonNumber
        }
        .eachCount()

    val visibleEpisodes = if (activeSeasonNumber == null) {
        content.episodes
    } else {
        content.episodes.filter { episode ->
            episode.season == activeSeasonNumber
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            top = 8.dp,
            bottom = 32.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item(
            key = "detail-poster"
        ) {
            DetailPoster(
                posterUrl = posterUrl,
                showName = show.name
            )
        }

        item(
            key = "detail-information"
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = show.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetadataChip(
                        text = "★ $ratingText",
                        highlighted = true
                    )

                    MetadataChip(
                        text = "Premiered $premiereText"
                    )
                }

                Text(
                    text = "Summary",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Text(
                        text = summaryText,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }

        if (content.cast.isNotEmpty()) {
            item(
                key = "detail-cast"
            ) {
                CastSection(
                    cast = content.cast
                )
            }
        }

        if (seasonNumbers.isNotEmpty()) {
            item(
                key = "detail-seasons"
            ) {
                SeasonSection(
                    seasonNumbers = seasonNumbers,
                    seasons = content.seasons,
                    episodeCounts = episodeCounts,
                    selectedSeasonNumber = activeSeasonNumber,
                    onSeasonSelected = onSeasonSelected
                )
            }
        }

        episodeSection(
            episodes = visibleEpisodes,
            selectedSeasonNumber = activeSeasonNumber
        )
    }
}