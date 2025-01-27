package com.seanof.sakugatomo.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.seanof.sakugatomo.util.Const.EMPTY

//https://www.sakugabooru.com/post.json

@kotlinx.serialization.Serializable
@Entity
data class SakugaPost(
    val actual_preview_height: Int = 0,
    val actual_preview_width: Int = 0,
    val author: String = EMPTY,
    val change: Int = 0,
    val created_at: Int = 0,
    val creator_id: Int = 0,
    val file_ext: String = EMPTY,
    val file_size: Int = 0,
    val file_url: String = EMPTY,
    val has_children: Boolean = false,
    val height: Int = 0,
    @PrimaryKey
    val id: Int,
    val jpeg_file_size: Int = 0,
    val jpeg_height: Int = 0,
    val jpeg_url: String = EMPTY,
    val jpeg_width: Int = 0,
    val md5: String = EMPTY,
    val preview_height: Int = 0,
    val preview_url: String = EMPTY,
    val preview_width: Int = 0,
    val rating: String = EMPTY,
    val sample_file_size: Int = 0,
    val sample_height: Int = 0,
    val sample_url: String = EMPTY,
    val sample_width: Int = 0,
    var saved: Boolean = false,
    val score: Int = 0,
    val source: String = EMPTY,
    val status: String = EMPTY,
    val tags: String = EMPTY,
    val updated_at: Int = 0,
    val width: Int = 0
)