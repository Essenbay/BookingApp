package com.example.bookingapp.data.repositories

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.auth.User
import kotlinx.coroutines.flow.StateFlow

interface AccessUser {
    val user: StateFlow<FirebaseUser?>
}