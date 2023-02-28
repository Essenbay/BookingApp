package com.example.bookingapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bookingapp.data.sources.FirebaseAuthSource
import com.example.bookingapp.util.FirebaseResult
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import com.example.bookingapp.BookingApplication
import com.example.bookingapp.data.repositories.FirebaseUserRepository

class AuthViewModel(private val firebaseUserRepository: FirebaseUserRepository) : ViewModel() {
    val userInputState: MutableStateFlow<AuthInputState> = MutableStateFlow(AuthInputState())
    val userData: StateFlow<FirebaseUser?> = firebaseUserRepository.getUser()

    fun register(email: String, password: String): LiveData<FirebaseResult<Boolean>> =
        firebaseUserRepository.register(email, password)

    fun login(email: String, password: String): LiveData<FirebaseResult<Boolean>> =
        firebaseUserRepository.login(email, password)

    fun signOut() = firebaseUserRepository.signOut()

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as BookingApplication)
                val userRepository = application.container.firebaseUserRepository
                AuthViewModel(userRepository)
            }
        }
    }
}