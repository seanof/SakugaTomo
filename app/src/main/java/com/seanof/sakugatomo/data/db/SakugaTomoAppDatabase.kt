package com.seanof.sakugatomo.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.seanof.sakugatomo.data.model.SakugaPost
import com.seanof.sakugatomo.data.model.SakugaTag

@Database(entities = [SakugaPost::class, SakugaTag::class], version = 1)
abstract class SakugaTomoAppDatabase : RoomDatabase() {
    abstract fun sakugaPostDao(): SakugaPostDao
}