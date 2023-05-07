package com.example.bookingapp.di

import com.example.bookingapp.data.datasource.FirebaseUserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class FirebaseAuthModule {
    @Provides
    fun provideFirebaseAuth(): FirebaseUserRepository = FirebaseUserRepository()
}