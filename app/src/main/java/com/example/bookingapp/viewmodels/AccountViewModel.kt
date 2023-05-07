package com.example.bookingapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookingapp.data.repositories.UserRepository
import com.example.bookingapp.util.FirebaseResult
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel() {
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

    suspend fun editUserInfo(fullName: String) = userRepository.editUserInfo(fullName)

    suspend fun editUserEmail(
        password: String,
        newEmail: String
    ) = userRepository.editUserEmail(password, newEmail)

    suspend fun editUserPassword(
        password: String,
        newPassword: String
    ) = userRepository.editUserPassword(password, newPassword)
}