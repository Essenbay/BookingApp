package com.example.bookingapp.data.sources

import android.util.Log
import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import androidx.lifecycle.liveData
import com.example.bookingapp.util.FirebaseResult

class FirebaseAuthSource {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()


    fun getAuthCurrentUser(): FirebaseUser? = auth.currentUser

    suspend fun register(email: String, password: String): FirebaseResult<String> =
        suspendCoroutine { cont ->
            Log.d("FirebaseAuthSource", "Auth is registering")
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    cont.resume(
                        FirebaseResult.Success(
                            auth.currentUser?.uid
                                ?: throw IllegalStateException("New user must not be null")
                        )
                    )
                }
                .addOnFailureListener {
                    cont.resume(FirebaseResult.Error(it))
                }
        }

    suspend fun login(email: String, password: String): FirebaseResult<Boolean> =
        suspendCoroutine { cont ->
            auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
                cont.resume(FirebaseResult.Success(true))
            }.addOnFailureListener {
                cont.resume(FirebaseResult.Error(it))
            }
        }

    fun signOut() = auth.signOut()

    suspend fun deleteUser(): FirebaseResult<String> = suspendCoroutine { cont ->
        val user: FirebaseUser =
            auth.currentUser ?: throw IllegalStateException("User is null")
        user.delete().addOnSuccessListener {
            cont.resume(
                FirebaseResult.Success(
                    auth.currentUser?.uid
                        ?: throw IllegalStateException("New user must not be null")
                )
            )
        }
            .addOnFailureListener {
                cont.resume(FirebaseResult.Error(it))
            }
    }
}