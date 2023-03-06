package com.example.bookingapp.util

import com.example.bookingapp.data.models.Establishment

sealed class SearchResult {
    object Empty : SearchResult()
    data class Error(val exception: Exception) : SearchResult()
    data class Success(val establishments: List<Establishment>) : SearchResult()
    object Loading : SearchResult()
}