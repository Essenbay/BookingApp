package com.example.bookingapp.data.firebaseauth

import com.google.firebase.auth.FirebaseUser

data class AuthState(
    val resultState: State = State.LOADING,
    val user: FirebaseUser? = null
) {
    companion object {
        enum class State {
            SUCCESSFUL, LOADING, FAILURE
        }
    }
}