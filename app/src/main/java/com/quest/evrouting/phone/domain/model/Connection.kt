package com.quest.evrouting.phone.domain.model

data class Connection(
    val typeName: String,
    val currentType: String,
    val powerKw: Double,
    val quantity: Int
)