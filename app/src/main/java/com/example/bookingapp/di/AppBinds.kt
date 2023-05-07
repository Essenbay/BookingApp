package com.example.bookingapp.di

import com.example.bookingapp.data.datasource.FirebaseUserRepository
import com.example.bookingapp.data.datasource.FirestoreRepository
import com.example.bookingapp.data.repositories.AccessUser
import com.example.bookingapp.data.repositories.EstablishmentsRepository
import com.example.bookingapp.data.repositories.ReviewRepository
import com.example.bookingapp.data.repositories.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(ActivityComponent::class)
abstract class AppBinds {
    @Binds
    @ActivityScoped
    abstract fun bindFirestore_to_EstablishmentRepository(
        impl: FirestoreRepository
    ): EstablishmentsRepository

    @Binds
    @ActivityScoped
    abstract fun bindFirestore_to_UserRepository(
        impl: FirebaseUserRepository
    ): UserRepository

    @Binds
    @ActivityScoped
    abstract fun bindUserRepository_to_accessUserIDRepository(
        impl: FirebaseUserRepository
    ): AccessUser

    @Binds
    @ActivityScoped
    abstract fun bindFirestore_to_ReviewRepository(
        impl: FirestoreRepository
    ): ReviewRepository
}