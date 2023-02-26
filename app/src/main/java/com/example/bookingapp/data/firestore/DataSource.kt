package com.example.bookingapp.data.firestore

import com.example.bookingapp.data.firestore.models.Result
import com.example.bookingapp.data.firestore.models.User

interface DataSource {
    suspend fun getUser(): Result<User>
    suspend fun editUser(userID: String)
    suspend fun deleteUser(userID: String)
}