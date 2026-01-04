package com.quest.evrouting.phone.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Place(
    val id: String,
    val primaryText: String,
    val secondaryText: String?,
//     val latitude: Double,
//     val longitude: Double
) : Parcelable
