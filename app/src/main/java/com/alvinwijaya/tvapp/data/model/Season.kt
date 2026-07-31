package com.alvinwijaya.tvapp.data.model

data class Season(
    val id: Int,
    val number: Int? = null,
    val name: String? = null,
    val episodeOrder: Int? = null,
    val premiereDate: String? = null,
    val endDate: String? = null,
    val image: ShowImage? = null,
    val summary: String? = null
)