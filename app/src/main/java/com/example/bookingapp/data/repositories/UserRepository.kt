package com.example.bookingapp.data.repositories

import androidx.lifecycle.LiveData
import com.example.bookingapp.data.models.User
import com.example.bookingapp.data.sources.FirebaseAuthSource
import com.example.bookingapp.data.sources.FirestoreSource
import com.example.bookingapp.util.FirebaseResult
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.StateFlow

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

    fun getUser():StateFlow<FirebaseUser?> = firebaseAuthSource.authUser

    fun register(email: String, password: String): LiveData<FirebaseResult<Boolean>> =
        firebaseAuthSource.register(email, password)

    fun login(email: String, password: String): LiveData<FirebaseResult<Boolean>> =
        firebaseAuthSource.login(email, password)

    fun signOut() = firebaseAuthSource.signOut()
}