package com.example.bookingapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bookingapp.BookingApplication
import com.example.bookingapp.data.repositories.UserRepository
import com.example.bookingapp.util.FirebaseResult
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext

//data class AuthInputState(
//    val emailInput: String = "",
//    val passwordInput: String = ""
//)

class AccountViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val coroutineContext = viewModelScope.coroutineContext + Dispatchers.IO
//    val userInputState: MutableStateFlow<AuthInputState> = MutableStateFlow(AuthInputState())
    val user: StateFlow<FirebaseUser?> = userRepository.user

    suspend fun register(
        fullName: String,
        email: String,
        password: String
    ): FirebaseResult<Boolean> = withContext(viewModelScope.coroutineContext) {
        userRepository.register(email, password, fullName)
    }


    suspend fun login(email: String, password: String): FirebaseResult<Boolean> =
        userRepository.login(email, password)


    fun signOut() = userRepository.signOut()

//    fun deleteAccount(): LiveData<FirebaseResult<Boolean>> = firebaseUserRepository.deleteUser()

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as BookingApplication)
                val userRepository = application.container.userRepository
                AccountViewModel(userRepository)
            }
        }
    }
}