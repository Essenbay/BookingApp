package com.example.bookingapp.util

import com.example.bookingapp.data.models.Establishment

sealed class SearchResult<out R> {
    data class Success<out T>(val result: T) : SearchResult<T>()
    data class Error(val exception: Exception) : SearchResult<Nothing>()
    object Loading : SearchResult<Nothing>()
    object Empty : SearchResult<Nothing>()
}