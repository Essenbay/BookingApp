package com.example.bookingapp.data

import com.example.bookingapp.data.repositories.FirebaseUserRepository
import com.example.bookingapp.data.repositories.FirestoreRepository

class AppContainer {
    val firebaseUserRepository by lazy {
        FirebaseUserRepository()
    }
    val firestoreRepository by lazy {
        FirestoreRepository()
    }
}