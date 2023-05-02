package com.example.bookingapp.di

import com.example.bookingapp.data.datasource.FirestoreRepository
import dagger.Module
import dagger.Provides

@Module
class FireStoreModule {
    @Provides
    fun provideFirestore():FirestoreRepository = FirestoreRepository()
}