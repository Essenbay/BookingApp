package com.example.bookingapp.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
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

    fun getUser(): StateFlow<FirebaseUser?> = firebaseAuthSource.authUser

    fun createUser(email: String, password: String): LiveData<FirebaseResult<Boolean>> = liveData {
        emit(
            startCreatingUser(email, password)
        )
    }

    private suspend fun startCreatingUser(
        email: String,
        password: String
    ): FirebaseResult<Boolean> {
        var result: FirebaseResult<Boolean> = FirebaseResult.Loading

        val userFirstname = "Assel"
        val userLastname = "Essenbay"

        val authResult = firebaseAuthSource.register(email, password)
        authResult.let {
            if (it is FirebaseResult.Success) {
                result = firestoreSource.createUser(it.data, userFirstname, userLastname)
            } else if (it is FirebaseResult.Error) {
                result = FirebaseResult.Error(it.exception)
            }
        }
        return result
    }


    fun login(email: String, password: String): LiveData<FirebaseResult<Boolean>> =
        firebaseAuthSource.login(email, password)

    fun signOut() = firebaseAuthSource.signOut()
}