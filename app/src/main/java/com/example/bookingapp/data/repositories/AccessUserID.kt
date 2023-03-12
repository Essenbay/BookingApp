package com.example.bookingapp.data.repositories

import com.google.firebase.auth.FirebaseUser

interface AccessUserID {
    fun getUserID(): String?
}