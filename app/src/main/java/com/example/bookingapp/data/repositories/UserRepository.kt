package com.example.bookingapp.data.repositories

import android.util.Log
import com.example.bookingapp.util.FirebaseResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.flow.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FirebaseUserRepository() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var _user: MutableStateFlow<FirebaseUser?> = MutableStateFlow(auth.currentUser)
    val user: StateFlow<FirebaseUser?> = _user.asStateFlow()
    fun getAuthCurrentUser(): FirebaseUser? = auth.currentUser

    init {
        auth.currentUser?.reload()
    }

    suspend fun register(
        email: String,
        password: String,
        fullName: String
    ): FirebaseResult<Boolean> =
        suspendCoroutine { cont ->
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    auth.currentUser?.updateProfile(
                        UserProfileChangeRequest.Builder().setDisplayName(fullName).build()
                    ) ?: throw IllegalStateException("Could not find currentUser")
                    _user.update {
                        auth.currentUser
                    }
                    cont.resume(FirebaseResult.Success(true))
                }
                .addOnFailureListener {
                    cont.resume(FirebaseResult.Error(it))
                }
        }

    suspend fun login(email: String, password: String): FirebaseResult<Boolean> =
        suspendCoroutine { cont ->
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    _user.update { auth.currentUser }
                    Log.d("FirebaseUserRepository", "Updating user: ${_user.value.toString()}")
                    cont.resume(FirebaseResult.Success(true))
                }
                .addOnFailureListener {
                    cont.resume(FirebaseResult.Error(it))
                }
        }

    fun signOut() {
        auth.signOut()
        _user.update {
            auth.currentUser
        }
    }
}