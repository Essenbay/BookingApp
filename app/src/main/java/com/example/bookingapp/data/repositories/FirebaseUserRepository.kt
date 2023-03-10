package com.example.bookingapp.data.repositories

import android.util.Log
import com.example.bookingapp.util.FirebaseResult
import com.google.firebase.auth.EmailAuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.flow.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

interface UserRepository {

    val user: StateFlow<FirebaseUser?>
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

class FirebaseUserRepository() : UserRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var _user: MutableStateFlow<FirebaseUser?> = MutableStateFlow(auth.currentUser)
    override val user: StateFlow<FirebaseUser?> = _user.asStateFlow()

    init {
        auth.currentUser?.reload()
    }

    override suspend fun register(
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

    override suspend fun editUserInfo(fullName: String): FirebaseResult<Boolean> =
        suspendCoroutine { cont ->
            _user.value?.updateProfile(
                UserProfileChangeRequest.Builder().setDisplayName(fullName).build()
            )
                ?.addOnSuccessListener {
                    cont.resume(FirebaseResult.Success(true))
                }
                ?.addOnFailureListener {
                    cont.resume(FirebaseResult.Error(it))
                }
        }

    override suspend fun editUserEmail(
        password: String,
        newEmail: String
    ): FirebaseResult<Boolean> {
        return when (val authResult = reauthenticate(password)) {
            is FirebaseResult.Success -> {
                return startEditAccountEmail(newEmail)
            }
            is FirebaseResult.Error -> {
                Log.d("FirebaseUserRepository", "auth level")
                FirebaseResult.Error(authResult.exception)
            }
        }
    }

    private suspend fun startEditAccountEmail(newEmail: String): FirebaseResult<Boolean> =
        suspendCoroutine { cont ->
            _user.value?.updateEmail(newEmail)
                ?.addOnSuccessListener {
                    cont.resume(FirebaseResult.Success(true))
                }
                ?.addOnFailureListener {
                    cont.resume(FirebaseResult.Error(it))
                }
        }


    override suspend fun editUserPassword(
        password: String, newPassword: String
    ): FirebaseResult<Boolean> {
        return when (val authResult = reauthenticate(password)) {
            is FirebaseResult.Success -> {
                return startChangePassword(newPassword)
            }
            is FirebaseResult.Error -> {
                FirebaseResult.Error(authResult.exception)
            }
        }
    }

    private suspend fun startChangePassword(newPassword: String): FirebaseResult<Boolean> =
        suspendCoroutine { cont ->
            _user.value?.updatePassword(newPassword)
                ?.addOnSuccessListener {
                    cont.resume(FirebaseResult.Success(true))
                }
                ?.addOnFailureListener {
                    cont.resume(FirebaseResult.Error(it))
                }
        }

    override suspend fun login(email: String, password: String): FirebaseResult<Boolean> =
        suspendCoroutine { cont ->
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    _user.update { auth.currentUser }
                    cont.resume(FirebaseResult.Success(true))
                }
                .addOnFailureListener {
                    cont.resume(FirebaseResult.Error(it))
                }
        }

    private suspend fun reauthenticate(password: String): FirebaseResult<Boolean> =
        suspendCoroutine { cont ->
            _user.value?.let { user ->
                val currEmail = user.email
                if (currEmail == null) cont.resume(FirebaseResult.Error(Exception("Email not found")))
                else {
                    val credentials = EmailAuthProvider.getCredential(currEmail, password)
                    user.reauthenticate(credentials)
                        .addOnSuccessListener {
                            cont.resume(FirebaseResult.Success(true))
                        }
                        .addOnFailureListener {
                            Log.d("FirebaseUserRepository", "${user.email} $it")
                            cont.resume(FirebaseResult.Error(it))
                        }
                }

            }
        }


    override fun signOut() {
        auth.signOut()
        _user.update {
            auth.currentUser
        }
    }
}