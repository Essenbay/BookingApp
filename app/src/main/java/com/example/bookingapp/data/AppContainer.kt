package com.example.bookingapp.data

import com.example.bookingapp.data.datasource.FirebaseUserRepository
import com.example.bookingapp.data.datasource.FirestoreRepository

class AppContainer {
    val userRepository by lazy {
        FirebaseUserRepository()
    }
    val storageRepository by lazy {
        FirestoreRepository()
    }
}