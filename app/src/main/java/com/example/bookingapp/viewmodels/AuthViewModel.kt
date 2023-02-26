package com.example.bookingapp.viewmodels

import androidx.lifecycle.ViewModel
import com.example.bookingapp.data.firebaseauth.AuthState
import com.example.bookingapp.data.firebaseauth.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel : ViewModel() {
    private val authRepository = AuthRepository.get()
    val userInputState: MutableStateFlow<AuthInputState> = MutableStateFlow(AuthInputState())
    val userData: StateFlow<AuthState> = authRepository.authState

    fun register(email: String, password: String) = authRepository.register(email, password)

    fun login(email: String, password: String) = authRepository.login(email, password)

    fun signOut() = authRepository.signOut()
}