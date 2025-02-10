package com.seanof.sakugatomo.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@kotlinx.serialization.Serializable
@Entity
data class SakugaTag(
    val ambiguous: Boolean = false,
    val count: Int = 0,
    @PrimaryKey
    val id: Int,
    val name: String,
    val type: Int = 0
)