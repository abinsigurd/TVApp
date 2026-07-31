package com.alvinwijaya.tvapp.data.model

data class Episode(
    val id: Int,
    val name: String,
    val season: Int? = null,
    val number: Int? = null,
    val airdate: String? = null,
    val runtime: Int? = null,
    val summary: String? = null,
    val image: ShowImage? = null
)