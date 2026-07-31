package com.alvinwijaya.tvapp.ui.detail.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.alvinwijaya.tvapp.data.model.Season
import com.alvinwijaya.tvapp.ui.components.RemoteImage

@Composable
internal fun SeasonSection(
    seasonNumbers: List<Int>,
    seasons: List<Season>,
    episodeCounts: Map<Int, Int>,
    selectedSeasonNumber: Int?,
    onSeasonSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SectionTitle(text = "Seasons")

        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = seasonNumbers,
                key = { seasonNumber ->
                    seasonNumber
                }
            ) { seasonNumber ->
                val season = seasons.firstOrNull { season ->
                    season.number == seasonNumber
                }

                SeasonCard(
                    seasonNumber = seasonNumber,
                    season = season,
                    episodeCount = episodeCounts[seasonNumber]
                        ?: season?.episodeOrder
                        ?: 0,
                    selected = seasonNumber ==
                            selectedSeasonNumber,
                    onClick = {
                        onSeasonSelected(seasonNumber)
                    }
                )
            }
        }
    }
}

@Composable
private fun SeasonCard(
    seasonNumber: Int,
    season: Season?,
    episodeCount: Int,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val seasonLabel = if (seasonNumber == 0) {
        "Specials"
    } else {
        "Season $seasonNumber"
    }

    val episodeLabel = when (episodeCount) {
        0 -> "No episodes"
        1 -> "1 episode"
        else -> "$episodeCount episodes"
    }

    val imageUrl = season?.image?.medium
        ?: season?.image?.original

    val border = if (selected) {
        BorderStroke(
            width = 2.dp,
            color = MaterialTheme.colorScheme.primary
        )
    } else {
        null
    }

    Card(
        onClick = onClick,
        modifier = modifier.width(112.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = border
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RemoteImage(
                model = imageUrl,
                contentDescription = "$seasonLabel poster",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f / 3f)
                    .clip(
                        RoundedCornerShape(
                            topStart = 14.dp,
                            topEnd = 14.dp
                        )
                    ),
                contentScale = ContentScale.Crop,
                fallbackText = "No image"
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = seasonLabel,
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = episodeLabel,
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}