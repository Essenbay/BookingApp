package com.example.bookingapp

import android.app.Application
import com.example.bookingapp.data.repositories.FirebaseUserRepository
import com.example.bookingapp.data.sources.FirebaseAuthSource
import com.example.bookingapp.data.sources.FirestoreSource

class BookingApplication : Application() {
    private val firebaseAuthSource = FirebaseAuthSource.get()
    private val firestoreSource = FirestoreSource.get()

    val firebaseUserRepository: FirebaseUserRepository by lazy {
        FirebaseUserRepository(firebaseAuthSource, firestoreSource)
    }
}