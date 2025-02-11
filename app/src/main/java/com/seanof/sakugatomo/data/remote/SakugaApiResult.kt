package com.seanof.sakugatomo.data.remote

sealed class SakugaApiResult<T>(val data: T ?= null, val error: String ?= null){
    class Success<T>(posts: T): SakugaApiResult<T>(data = posts)
    class Error<T>(error: String): SakugaApiResult<T>(error = error)
    class Loading<T>: SakugaApiResult<T>()
}