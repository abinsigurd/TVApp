package com.alvinwijaya.tvapp.data.model

data class ShowDetailContent(
    val show: Show,
    val cast: List<CastCredit> = emptyList(),
    val seasons: List<Season> = emptyList(),
    val episodes: List<Episode> = emptyList()
)