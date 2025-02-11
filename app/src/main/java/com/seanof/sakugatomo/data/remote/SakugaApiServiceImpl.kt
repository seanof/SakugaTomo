package com.seanof.sakugatomo.data.remote

import com.seanof.sakugatomo.data.model.SakugaPost
import com.seanof.sakugatomo.data.model.SakugaTag
import com.seanof.sakugatomo.util.Const.LOG_MSG_SOMETHING_WENT_WRONG
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SakugaApiServiceImpl @Inject constructor(private val httpClient: HttpClient) : SakugaApiService {

    override fun getSakugaPosts(limit: Int): Flow<SakugaApiResult<List<SakugaPost>>> = flow {
        emit(SakugaApiResult.Loading())
        try {
            emit(SakugaApiResult.Success(httpClient.get(POST_PATH) {
                 parameter(PARAM_LIMIT, limit)
            }.body()))
        } catch (e:Exception) {
            e.printStackTrace()
            emit(SakugaApiResult.Error(e.message ?: LOG_MSG_SOMETHING_WENT_WRONG))
        }
    }

    override fun getPopularSakugaPosts(): Flow<SakugaApiResult<List<SakugaPost>>> = flow {
        emit(SakugaApiResult.Loading())
        try {
            emit(SakugaApiResult.Success(httpClient.get(POPULAR_BY_MONTH_PATH) {
            }.body()))
        } catch (e:Exception) {
            e.printStackTrace()
            emit(SakugaApiResult.Error(e.message ?: LOG_MSG_SOMETHING_WENT_WRONG))
        }
    }

    override fun searchSakugaPosts(tags: String): Flow<SakugaApiResult<List<SakugaPost>>> = flow {
        emit(SakugaApiResult.Loading())
        try {
            emit(SakugaApiResult.Success(httpClient.get(POST_PATH) {
                parameter(PARAM_TAGS, tags)
            }.body()))
        } catch (e:Exception) {
            e.printStackTrace()
            emit(SakugaApiResult.Error(e.message ?: LOG_MSG_SOMETHING_WENT_WRONG))
        }
    }

    override fun getSakugaTags(): Flow<SakugaApiResult<List<SakugaTag>>> = flow {
        emit(SakugaApiResult.Loading())
        try {
            emit(SakugaApiResult.Success(httpClient.get(TAG_PATH) {
                parameter(PARAM_LIMIT, 0)
            }.body()))
        } catch (e:Exception) {
            e.printStackTrace()
            emit(SakugaApiResult.Error(e.message ?: LOG_MSG_SOMETHING_WENT_WRONG))
        }
    }

    companion object {
        private const val POST_PATH = "/post.json"
        private const val POPULAR_BY_MONTH_PATH = "/post/popular_by_month.json"
        private const val TAG_PATH = "/tag.json"
        private const val PARAM_LIMIT = "limit"
        private const val PARAM_TAGS = "tags"
    }
}