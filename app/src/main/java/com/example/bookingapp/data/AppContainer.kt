package com.example.bookingapp.data

import com.example.bookingapp.data.repositories.EstablishmentRepository
import com.example.bookingapp.data.repositories.FirebaseUserRepository
import com.example.bookingapp.data.sources.FirestoreSource

class AppContainer {
    private val firestoreSource = FirestoreSource()

    val firebaseUserRepository by lazy {
        FirebaseUserRepository()
    }
    val establishmentRepository by lazy {
        EstablishmentRepository(firebaseUserRepository, firestoreSource)
    }
}