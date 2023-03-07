package com.example.bookingapp.data

import com.example.bookingapp.data.repositories.FirebaseUserRepository
import com.example.bookingapp.data.repositories.FirestoreRepository

class AppContainer {
    val userRepository by lazy {
        FirebaseUserRepository()
    }
    val storageRepository by lazy {
        FirestoreRepository()
    }
}