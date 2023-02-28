package com.example.bookingapp

import android.app.Application
import com.example.bookingapp.data.AppContainer
import com.example.bookingapp.data.repositories.FirebaseUserRepository
import com.example.bookingapp.data.sources.FirebaseAuthSource
import com.example.bookingapp.data.sources.FirestoreSource

class BookingApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainer()
    }
}