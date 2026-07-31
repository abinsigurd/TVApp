package com.alvinwijaya.tvapp.ui.detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.alvinwijaya.tvapp.data.model.CastCredit
import com.alvinwijaya.tvapp.ui.components.RemoteImage

@Composable
internal fun CastSection(
    cast: List<CastCredit>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SectionTitle(text = "Cast")

        LazyRow(
            contentPadding = PaddingValues(
                horizontal = 20.dp
            ),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = cast,
                key = { credit ->
                    "${credit.person.id}-${credit.character.id}"
                }
            ) { credit ->
                CastCard(
                    castCredit = credit
                )
            }
        }
    }
}

@Composable
private fun CastCard(
    castCredit: CastCredit,
    modifier: Modifier = Modifier
) {
    val imageUrl = castCredit.person.image?.medium
        ?: castCredit.character.image?.medium

    Column(
        modifier = modifier.width(104.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        RemoteImage(
            model = imageUrl,
            contentDescription =
                "${castCredit.person.name} as ${castCredit.character.name}",
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2f / 3f)
                .clip(RoundedCornerShape(14.dp)),
            contentScale = ContentScale.Crop,
            fallbackText = "No image"
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .heightIn(min = 58.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = castCredit.person.name,
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )

            Text(
                text = castCredit.character.name,
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