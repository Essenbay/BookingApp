package com.example.bookingapp.di

import com.example.bookingapp.data.datasource.FirebaseUserRepository
import dagger.Module
import dagger.Provides

@Module
class FirebaseAuthModule {
    @Provides
    fun provideFirebaseAuth(): FirebaseUserRepository = FirebaseUserRepository()
}