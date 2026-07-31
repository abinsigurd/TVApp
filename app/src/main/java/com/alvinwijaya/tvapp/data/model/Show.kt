package com.alvinwijaya.tvapp.data.model

import com.google.gson.annotations.SerializedName

data class Show(
    val id: Int,
    val name: String,
    val url: String? = null,
    val summary: String? = null,
    val premiered: String? = null,
    val rating: Rating? = null,
    val image: ShowImage? = null,
    @SerializedName("_embedded")
    val embedded: EmbeddedShowData? = null
)

data class EmbeddedShowData(
    val cast: List<CastCredit>? = null
)

data class Rating(
    val average: Double? = null
)

data class ShowImage(
    val medium: String? = null,
    val original: String? = null
)