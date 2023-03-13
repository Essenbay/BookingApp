package com.example.bookingapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bookingapp.BookingApplication
import com.example.bookingapp.data.repositories.AccessUser

class ReservationContainerViewModel(private val userRepository: AccessUser) : ViewModel() {
    fun isUserSignedIn(): Boolean {
        return userRepository.user.value == null
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as BookingApplication)
                val firebaseUserID = application.container.userRepository
                ReservationContainerViewModel(firebaseUserID)
            }
        }
    }
}