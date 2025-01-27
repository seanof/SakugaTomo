package com.seanof.sakugatomo.data.remote

import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@dagger.Module
@InstallIn(SingletonComponent::class)
object SakugaApiModule {

    @Singleton
    @Provides
    fun provideHttpClient(): HttpClient {
        return HttpClient(Android) {
            install(DefaultRequest) {
                url("https://sakugabooru.com")
                header(HttpHeaders.ContentType, ContentType.Application.Json)
            }
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
            install(HttpCache)
        }
    }

    @Singleton
    @Provides
    fun provideApiService(httpClient: HttpClient): SakugaApiService = SakugaApiServiceImpl(httpClient)

    @Provides
    fun provideDispatcher(): CoroutineDispatcher = Dispatchers.Default
}