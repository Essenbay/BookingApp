package com.example.bookingapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bookingapp.BookingApplication
import com.example.bookingapp.data.models.Establishment
import com.example.bookingapp.data.repositories.EstablishmentRepository
import com.example.bookingapp.data.repositories.FirebaseUserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val firebaseEstablishmentRepository: EstablishmentRepository):ViewModel() {
    private var _establishments: MutableStateFlow<List<Establishment>> = MutableStateFlow(emptyList())
    val establishments = _establishments.asStateFlow()

    init {
        viewModelScope.launch {

        }
    }
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as BookingApplication)
                val establishmentRepository = application.container.firebaseUserRepository
                AccountViewModel(establishmentRepository)
            }
        }
    }
}