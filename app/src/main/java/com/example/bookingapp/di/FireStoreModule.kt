package com.example.bookingapp.di

import com.example.bookingapp.data.datasource.FirestoreRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class FireStoreModule {
    @Provides
    fun provideFirestore():FirestoreRepository = FirestoreRepository()
}