package com.example.bookingapp.data

import com.example.bookingapp.data.repositories.EstablishmentRepository
import com.example.bookingapp.data.repositories.FirebaseUserRepository
import com.example.bookingapp.data.sources.FirebaseAuthSource
import com.example.bookingapp.data.sources.FirestoreSource

class AppContainer {
    private val firebaseAuthSource = FirebaseAuthSource()
    private val firestoreSource = FirestoreSource()
    val firebaseUserRepository by lazy {
        FirebaseUserRepository(firebaseAuthSource, firestoreSource)
    }
    val establishmentRepository by lazy {
        EstablishmentRepository(firebaseAuthSource, firestoreSource)
    }
}