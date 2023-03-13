package com.example.bookingapp.data.repositories

import com.example.bookingapp.util.FirebaseResult
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.StateFlow

interface UserRepository : AccessUser {
    suspend fun register(
        email: String,
        password: String,
        fullName: String
    ): FirebaseResult<Boolean>

    suspend fun editUserInfo(
        fullName: String
    ): FirebaseResult<Boolean>

    suspend fun editUserEmail(
        password: String, newEmail: String
    ): FirebaseResult<Boolean>

    suspend fun editUserPassword(
        password: String, newPassword: String
    ): FirebaseResult<Boolean>

    suspend fun login(email: String, password: String): FirebaseResult<Boolean>

    fun signOut()
}