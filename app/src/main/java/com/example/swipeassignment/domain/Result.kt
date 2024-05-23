package com.example.swipeassignment.domain

sealed class Result<T>(var data:T?= null, val message: String? = null) {
    //Sealed class for handling responses from the repository
    class Success<T>(data: T): Result<T>(data)
    class Error<T>(data: T? = null, message: String?): Result<T>(data,message)
    class Loading<T>(): Result<T>()
}