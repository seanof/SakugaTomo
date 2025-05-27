package com.seanof.sakugatomo.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.seanof.sakugatomo.util.Const.EMPTY

//https://www.sakugabooru.com/post.json

@kotlinx.serialization.Serializable
@Entity
data class SakugaPost(
    val author: String = EMPTY,
    val file_size: Int = 0,
    val file_url: String = EMPTY,
    val height: Int = 0,
    @PrimaryKey
    val id: Int,
    val md5: String = EMPTY,
    val preview_height: Int = 0,
    val preview_url: String = EMPTY,
    val preview_width: Int = 0,
    val rating: String = EMPTY,
    var saved: Boolean = false,
    val score: Int = 0,
    val source: String = EMPTY,
    val status: String = EMPTY,
    val tags: String = EMPTY,
    var sourceTitle: String = EMPTY,
    val width: Int = 0
)