package com.seanof.sakugatomo.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.seanof.sakugatomo.data.model.SakugaPost
import com.seanof.sakugatomo.data.model.SakugaTag
import kotlinx.coroutines.flow.Flow

@Dao
interface SakugaPostDao {
    @Query("SELECT * from SakugaPost ORDER BY id DESC")
    fun getSakugaPosts(): Flow<List<SakugaPost>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(sakugaPost: SakugaPost)

    @Query("SELECT * from SakugaTag ORDER BY name ASC")
    fun getSakugaTags(): Flow<List<SakugaTag>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTags(sakugaTag: List<SakugaTag>)

    @Delete
    suspend fun delete(sakugaPost: SakugaPost)
}
