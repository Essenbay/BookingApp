package com.example.bookingapp.data.sources

import android.content.Context
import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import androidx.lifecycle.liveData
import com.example.bookingapp.util.FirebaseResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope

class FirebaseAuthSource {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var _authUser: MutableStateFlow<FirebaseUser?> = MutableStateFlow(auth.currentUser)
    val authUser = _authUser.asStateFlow()

    fun register(email: String, password: String): LiveData<FirebaseResult<Boolean>> = liveData {
        emit(
            suspendCoroutine { cont ->
                auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
                    _authUser.update { auth.currentUser }

                    cont.resume(FirebaseResult.Success(true))
                }.addOnFailureListener {
                    cont.resume(FirebaseResult.Error(it))
                }
            }
        )
    }

    fun login(email: String, password: String): LiveData<FirebaseResult<Boolean>> = liveData {
        emit(
            suspendCoroutine { cont ->
                auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
                    _authUser.update { auth.currentUser }
                    cont.resume(FirebaseResult.Success(true))
                }.addOnFailureListener {
                    cont.resume(FirebaseResult.Error(it))
                }
            }
        )
    }

    fun signOut() {
        auth.signOut()
        _authUser.update { auth.currentUser }
    }
}