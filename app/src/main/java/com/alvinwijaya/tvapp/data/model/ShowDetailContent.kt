package com.alvinwijaya.tvapp.data.model

data class ShowDetailContent(
    val show: Show,
    val cast: List<CastCredit> = emptyList()
)