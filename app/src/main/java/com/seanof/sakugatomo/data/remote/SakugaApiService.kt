package com.seanof.sakugatomo.data.remote

import com.seanof.sakugatomo.data.model.SakugaPost
import com.seanof.sakugatomo.data.model.SakugaTag
import kotlinx.coroutines.flow.Flow

interface SakugaApiService {
    fun getSakugaPosts(limit: Int): Flow<SakugaApiResult<List<SakugaPost>>>
    fun getPopularSakugaPosts(): Flow<SakugaApiResult<List<SakugaPost>>>
    fun searchSakugaPosts(tags: String): Flow<SakugaApiResult<List<SakugaPost>>>
    fun getSakugaTags(): Flow<SakugaApiResult<List<SakugaTag>>>
}