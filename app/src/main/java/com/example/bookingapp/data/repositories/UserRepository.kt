package com.example.bookingapp.data.repositories

import com.example.bookingapp.data.models.User
import com.example.bookingapp.data.sources.FirebaseAuthSource
import com.example.bookingapp.data.sources.FirestoreSource

interface UserRepository {
    suspend fun createUser(user: User)
}

class FirebaseUserRepository(
    private val firebaseAuthSource: FirebaseAuthSource,
    private val firestoreSource: FirestoreSource
) : UserRepository {
    override suspend fun createUser(user: User) {
        TODO("Not yet implemented")
    }
}