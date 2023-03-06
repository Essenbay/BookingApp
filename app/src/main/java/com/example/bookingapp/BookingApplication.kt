package com.example.bookingapp

import android.app.Application
import com.example.bookingapp.data.AppContainer

//Todo: Add documentation to code
class BookingApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainer()
    }
}