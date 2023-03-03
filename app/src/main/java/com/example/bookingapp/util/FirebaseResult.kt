package com.example.bookingapp.util

import com.example.bookingapp.data.Result

sealed class FirebaseResult<out R> {
    data class Success<out T>(val data: T) : FirebaseResult<T>()
    data class Error(val exception: Exception) : FirebaseResult<Nothing>()
}