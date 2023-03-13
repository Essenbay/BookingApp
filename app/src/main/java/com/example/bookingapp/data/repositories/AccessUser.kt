package com.example.bookingapp.data.repositories

import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.StateFlow

interface AccessUser {
    val user: StateFlow<FirebaseUser?>
}