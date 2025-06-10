package com.projects.core

sealed class Response<T> {
    class Success<T>(val data: T) : Response<T>()
    class Error<T>(val message: String) : Response<T>()
    class Loading<T> : Response<T>()
}