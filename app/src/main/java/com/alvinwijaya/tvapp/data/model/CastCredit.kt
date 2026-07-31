package com.alvinwijaya.tvapp.data.model

data class CastCredit(
    val person: Person,
    val character: Character
)

data class Person(
    val id: Int,
    val name: String,
    val image: ShowImage? = null
)

data class Character(
    val id: Int,
    val name: String,
    val image: ShowImage? = null
)