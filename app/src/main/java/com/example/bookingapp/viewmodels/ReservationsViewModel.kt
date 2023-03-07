package com.example.bookingapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bookingapp.BookingApplication
import com.example.bookingapp.data.models.Reservation
import com.example.bookingapp.data.repositories.StoreRepository
import com.example.bookingapp.util.SearchResult
import kotlinx.coroutines.flow.MutableStateFlow


class ReservationsViewModel(storeRepository: StoreRepository) : ViewModel() {
    private var _reservations: MutableStateFlow<List<Reservation>> = MutableStateFlow(emptyList())
    private var _filteredReservations: MutableStateFlow<SearchResult> =
        MutableStateFlow(SearchResult.Loading)

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as BookingApplication)
                val firestoreRepository = application.container.storageRepository
                ReservationsViewModel(firestoreRepository)
            }
        }
    }
}