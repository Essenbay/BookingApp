package com.example.bookingapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bookingapp.util.FirebaseResult
import kotlinx.coroutines.flow.MutableStateFlow
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import com.example.bookingapp.BookingApplication
import com.example.bookingapp.data.models.User
import com.example.bookingapp.data.repositories.FirebaseUserRepository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class AccountViewModel(private val firebaseUserRepository: FirebaseUserRepository) : ViewModel() {
    val userInputState: MutableStateFlow<AuthInputState> = MutableStateFlow(AuthInputState())
    val user: SharedFlow<User?> = firebaseUserRepository.user

    suspend fun register(
        fullName: String,
        email: String,
        phoneNumber: String,
        password: String
    ): FirebaseResult<Boolean> =
        firebaseUserRepository.createUser(fullName, email, phoneNumber, password)

    suspend fun login(email: String, password: String): FirebaseResult<Boolean> =
        firebaseUserRepository.login(email, password)


    fun signOut() = viewModelScope.launch {
        firebaseUserRepository.signOut()
    }

//    fun deleteAccount(): LiveData<FirebaseResult<Boolean>> = firebaseUserRepository.deleteUser()

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as BookingApplication)
                val userRepository = application.container.firebaseUserRepository
                AccountViewModel(userRepository)
            }
        }
    }
}