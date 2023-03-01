package com.example.bookingapp.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.bookingapp.data.models.User
import com.example.bookingapp.data.sources.FirebaseAuthSource
import com.example.bookingapp.data.sources.FirestoreSource
import com.example.bookingapp.util.FirebaseResult
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import java.lang.Exception
import kotlin.coroutines.suspendCoroutine

class FirebaseUserRepository(
    private val firebaseAuthSource: FirebaseAuthSource,
    private val firestoreSource: FirestoreSource
) {
    private var _user: MutableSharedFlow<User?> = MutableStateFlow(null)
    val user: SharedFlow<User?> = _user

    private suspend fun getUser(): User? {
        val authUser = firebaseAuthSource.getAuthCurrentUser() ?: return null
        val user = firestoreSource.getUser(authUser.uid)
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
                        _user.emit(firestoreResult.data)
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
        _user.emit(null)
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