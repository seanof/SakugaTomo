package com.seanof.sakugatomo.data.db

import com.seanof.sakugatomo.data.model.SakugaPost
import com.seanof.sakugatomo.data.model.SakugaTag
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SakugaPostRepository @Inject constructor(private val sakugaPostDao: SakugaPostDao) {
    val sakugaPosts: Flow<List<SakugaPost>> = sakugaPostDao.getSakugaPosts()
    val sakugaTags: Flow<List<SakugaTag>> = sakugaPostDao.getSakugaTags()

    suspend fun insert(sakugaPost: SakugaPost) {
        sakugaPostDao.insert(sakugaPost)
    }

    suspend fun insertTags(sakugaTag: List<SakugaTag>) {
        sakugaPostDao.insertTags(sakugaTag)
    }

    suspend fun delete(sakugaPost: SakugaPost) {
        sakugaPostDao.delete(sakugaPost)
    }
}