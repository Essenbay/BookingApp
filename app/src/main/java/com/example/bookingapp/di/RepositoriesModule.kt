package com.example.bookingapp.di

import com.example.bookingapp.data.datasource.FirebaseUserRepository
import com.example.bookingapp.data.datasource.FirestoreRepository
import com.example.bookingapp.data.repositories.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoriesModule {
    @Provides
    fun provideAccessUser(firebaseUserRepository: FirebaseUserRepository): AccessUser =
        firebaseUserRepository

    @Provides
    fun provideUserRepository(firebaseUserRepository: FirebaseUserRepository): UserRepository =
        firebaseUserRepository

    @Provides
    fun provideEstablishmentsRepository(firestoreRepository: FirestoreRepository): EstablishmentsRepository =
        firestoreRepository

    @Provides
    fun provideEstablishmentRepository(firestoreRepository: FirestoreRepository): EstablishmentRepository =
        firestoreRepository

    @Provides
    fun provideReceiveReservations(firestoreRepository: FirestoreRepository): ReceiveReservations =
        firestoreRepository

    @Provides
    fun provideReviewRepository(firestoreRepository: FirestoreRepository): ReviewRepository =
        firestoreRepository


}