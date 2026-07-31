package com.alvinwijaya.tvapp.ui.list.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.alvinwijaya.tvapp.data.model.Show
import java.util.Locale

@Composable
internal fun ShowCard(
    show: Show,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val posterUrl = show.image?.medium

    val ratingText = show.rating?.average?.let { rating ->
        String.format(
            Locale.getDefault(),
            "%.1f",
            rating
        )
    } ?: "N/A"

    ElevatedCard(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 6.dp
        )
    ) {
        Column {
            PosterWithRating(
                posterUrl = posterUrl,
                showName = show.name,
                ratingText = ratingText
            )

            Text(
                text = show.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                style = MaterialTheme.typography.titleMedium.copy(
                    lineHeight = 20.sp
                ),
                fontWeight = FontWeight.Bold,
                minLines = 2,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Start
            )
        }
    }
}

@Composable
private fun PosterWithRating(
    posterUrl: String?,
    showName: String,
    ratingText: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(210f / 295f)
    ) {
        if (posterUrl != null) {
            AsyncImage(
                model = posterUrl,
                contentDescription = "$showName poster",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No poster",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        RatingBadge(
            ratingText = ratingText,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
        )
    }
}

@Composable
private fun RatingBadge(
    ratingText: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.scrim.copy(
            alpha = 0.78f
        ),
        contentColor = Color.White,
        border = BorderStroke(
            width = 1.dp,
            color = Color.White.copy(alpha = 0.24f)
        ),
        shadowElevation = 4.dp
    ) {
        Text(
            text = "★ $ratingText",
            modifier = Modifier.padding(
                horizontal = 12.dp,
                vertical = 7.dp
            ),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )
    }
}