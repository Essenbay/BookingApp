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
import com.example.bookingapp.util.FirebaseResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class SearchResult {
    object Empty : SearchResult()
    data class Error(val exception: Exception) : SearchResult()
    object Success : SearchResult()
}

class HomeViewModel(private val firebaseEstablishmentRepository: EstablishmentRepository) :
    ViewModel() {

    private var _establishments: MutableStateFlow<FirebaseResult<List<Establishment>>> =
        MutableStateFlow(
            FirebaseResult.Success(
                emptyList()
            )
        )
    val establishments = _establishments.asStateFlow()

    init {
        getAllEstablishments()
    }

    fun getAllEstablishments() {
        viewModelScope.launch {
            _establishments.update { firebaseEstablishmentRepository.getEstablishments() }
        }
    }

    suspend fun searchEstablishments(query: String): SearchResult {
        return when (val result = firebaseEstablishmentRepository.searchEstablishments(query)) {
            is FirebaseResult.Success -> {
                _establishments.update { result }
                if (result.data.isEmpty()) SearchResult.Empty
                else SearchResult.Success
            }
            is FirebaseResult.Error -> SearchResult.Error(result.exception)
        }
    }


    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as BookingApplication)
                val establishmentRepository = application.container.establishmentRepository
                HomeViewModel(establishmentRepository)
            }
        }
    }
}