package com.example.bookingapp.data.repositories

import android.content.Context
import android.util.Log
import com.example.bookingapp.data.models.User
import com.example.bookingapp.data.sources.FirebaseAuthSource
import com.example.bookingapp.data.sources.FirestoreSource
import com.example.bookingapp.util.FirebaseResult
import com.google.firebase.auth.FirebaseUser
import java.lang.Exception

class FirebaseUserRepository(
    private val firebaseAuthSource: FirebaseAuthSource,
    private val firestoreSource: FirestoreSource
) {
    suspend fun getUser(): User? {
        val authUser: FirebaseUser = firebaseAuthSource.getAuthCurrentUser() ?: return null
        return firestoreSource.getUser(authUser.uid)
    }

    suspend fun createUser(
        fullName: String,
        email: String,
        phoneNumber: String,
        password: String
    ): FirebaseResult<Boolean> {
        var result: FirebaseResult<Boolean> = FirebaseResult.Error(Exception("Unknown exception"))
        val authResult = firebaseAuthSource.register(email, password)
        authResult.let {
            if (it is FirebaseResult.Success) {
                val createdUser = firestoreSource.createUser(it.data, fullName, phoneNumber)
                createdUser.let { firestoreResult ->
                    if (firestoreResult is FirebaseResult.Success) {
                        result = FirebaseResult.Success(true)
                    } else if (firestoreResult is FirebaseResult.Error) {
                        result = FirebaseResult.Error(firestoreResult.exception)
                    }
                }
            } else if (it is FirebaseResult.Error) {
                result = FirebaseResult.Error(it.exception)
            }
        }
        return result
    }


    suspend fun login(email: String, password: String): FirebaseResult<Boolean> =
        firebaseAuthSource.login(email, password)

    suspend fun signOut() {
        firebaseAuthSource.signOut()
    }

//    suspend fun deleteUser(): FirebaseResult<Boolean> {
//        var result: FirebaseResult<Boolean> = FirebaseResult.Loading
//        val authResult = firebaseAuthSource.deleteUser()
//        authResult.let {
//            if (it is FirebaseResult.Success) {
//                result = firestoreSource.deleteUser(it.data)
//            } else if (it is FirebaseResult.Error) {
//                result = FirebaseResult.Error(it.exception)
//            }
//        }
//        return result
//    }
}