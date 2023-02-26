package com.example.bookingapp

import android.app.Application
import com.example.bookingapp.data.firebaseauth.AuthRepository

class BookingApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AuthRepository.initialize(this)
    }
}