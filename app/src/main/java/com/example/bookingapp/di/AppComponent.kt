package com.example.bookingapp.di

import com.example.bookingapp.views.MainActivity
import dagger.Component

@Component(modules = [FireStoreModule::class, FirebaseAuthModule::class])
interface AppComponent {
    fun inject(activity: MainActivity)
}