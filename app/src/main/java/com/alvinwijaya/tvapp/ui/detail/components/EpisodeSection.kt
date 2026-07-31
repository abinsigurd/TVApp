package com.alvinwijaya.tvapp.ui.detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.alvinwijaya.tvapp.data.model.Episode
import com.alvinwijaya.tvapp.ui.components.RemoteImage

internal fun LazyListScope.episodeSection(
    episodes: List<Episode>,
    selectedSeasonNumber: Int?
) {
    val sectionTitle = when (selectedSeasonNumber) {
        null -> "Episodes"
        0 -> "Special Episodes"
        else -> "Season $selectedSeasonNumber Episodes"
    }

    item(
        key = "episode-header-$selectedSeasonNumber"
    ) {
        SectionTitle(text = sectionTitle)
    }

    if (episodes.isEmpty()) {
        item(
            key = "episode-empty-$selectedSeasonNumber"
        ) {
            EmptyEpisodeContent()
        }

        return
    }

    items(
        items = episodes,
        key = { episode ->
            "episode-${episode.id}"
        }
    ) { episode ->
        EpisodeCard(
            episode = episode,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
    }
}

@androidx.compose.runtime.Composable
private fun EpisodeCard(
    episode: Episode,
    modifier: Modifier = Modifier
) {
    val thumbnailUrl = episode.image?.original
        ?: episode.image?.medium

    val metadata = buildList {
        add(episode.episodeCode())

        episode.airdate
            ?.takeIf { it.isNotBlank() }
            ?.let(::add)

        episode.runtime
            ?.let { runtime ->
                add("$runtime min")
            }
    }.joinToString(separator = " • ")

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RemoteImage(
                model = thumbnailUrl,
                contentDescription =
                    "${episode.name} episode thumbnail",
                modifier = Modifier
                    .width(132.dp)
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop,
                fallbackText = "No image"
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = episode.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = metadata,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@androidx.compose.runtime.Composable
private fun EmptyEpisodeContent() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Text(
            text = "No episodes are available for this season.",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun Episode.episodeCode(): String {
    val seasonPart = season
        ?.toString()
        ?.padStart(
            length = 2,
            padChar = '0'
        )
        ?: "--"

    val episodePart = number
        ?.toString()
        ?.padStart(
            length = 2,
            padChar = '0'
        )
        ?: "--"

    return "S${seasonPart}E${episodePart}"
}