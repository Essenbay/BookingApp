package com.example.bookingapp.data.firebaseauth

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AuthRepository private constructor(
    context: Context, private val coroutineScope: CoroutineScope = GlobalScope
) {
    private var _authState: MutableStateFlow<AuthState> = MutableStateFlow(AuthState())
    val authState = _authState.asStateFlow()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    init {
        val user = auth.currentUser
        if (user != null) {
            _authState.update {
                it.copy(user = auth.currentUser, resultState = AuthState.Companion.State.SUCCESSFUL)
            }
        }
    }

    fun register(email: String, password: String) {
        _authState.update {
            it.copy(resultState = AuthState.Companion.State.LOADING)
        }
        auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
            _authState.update {
                it.copy(
                    user = auth.currentUser, resultState = AuthState.Companion.State.SUCCESSFUL
                )
            }
        }.addOnFailureListener {
            _authState.update {
                it.copy(resultState = AuthState.Companion.State.FAILURE)
            }
        }
    }

    fun login(email: String, password: String) {
        _authState.update {
            it.copy(resultState = AuthState.Companion.State.LOADING)
        }
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
            _authState.update {
                it.copy(
                    user = auth.currentUser, resultState = AuthState.Companion.State.SUCCESSFUL
                )
            }
        }.addOnFailureListener {
            _authState.update {
                it.copy(resultState = AuthState.Companion.State.FAILURE)
            }
        }
    }

    fun signOut() {
        auth.signOut()
        _authState.update {
            it.copy(
                user = null, resultState = AuthState.Companion.State.LOADING
            )
        }

    }

    companion object {
        private var INSTANCE: AuthRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = AuthRepository(context)
            }
        }

        fun get(): AuthRepository {
            return INSTANCE ?: throw IllegalStateException("CrimeRepository must be initialized")
        }
    }
}