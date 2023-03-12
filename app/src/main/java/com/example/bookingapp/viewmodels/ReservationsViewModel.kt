package com.example.bookingapp.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bookingapp.BookingApplication
import com.example.bookingapp.data.models.Establishment
import com.example.bookingapp.data.models.Reservation
import com.example.bookingapp.data.repositories.AccessUserID
import com.example.bookingapp.data.repositories.EstablishmentsRepository
import com.example.bookingapp.data.repositories.ReceiveReservations
import com.example.bookingapp.util.FirebaseResult
import com.example.bookingapp.util.SearchResult
import com.example.bookingapp.util.UserNotSignedIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ReservationsViewModel(
    private val reservationRepository: ReceiveReservations,
    private val userID: AccessUserID
) : ViewModel() {
    private var _reservations: List<Reservation> = emptyList()
    private var _filteredReservations: MutableStateFlow<SearchResult<List<Reservation>>> =
        MutableStateFlow(SearchResult.Loading)
    val filteredReservations = _filteredReservations.asStateFlow()

    init {
        getReservations()
    }
    fun getReservations() = viewModelScope.launch {
        val userID = userID.getUserID()
        if (userID == null) {
            SearchResult.Error(UserNotSignedIn())
        } else {
            when (val result = reservationRepository.getReservationByUser(userID)) {
                is FirebaseResult.Success -> {
                    _reservations = result.data
                    searchReservations("")
                }
                is FirebaseResult.Error -> _filteredReservations.update {
                    SearchResult.Error(result.exception)
                }
            }
        }
    }
    suspend fun searchReservations(query: String?) {
        _filteredReservations.update { startSearchReservations(query) }
    }
    private suspend fun startSearchReservations(query: String?): SearchResult<List<Reservation>> =
        suspendCoroutine { cont ->
            if (query == null || query.isBlank())
                cont.resume(SearchResult.Success(_reservations))
            else {
                val resultList = mutableListOf<Reservation>()
                for (e in _reservations) {
                    //Todo: Add to Reservation class establishment whole not only id
//                    if (e.name.lowercase().contains(query.lowercase()))
                    resultList.add(e)
                }
                if (resultList.isEmpty()) cont.resume(SearchResult.Empty)
                else cont.resume(SearchResult.Success(resultList))            }
        }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as BookingApplication)
                val firestoreRepository = application.container.storageRepository
                val firebaseUserID = application.container.userRepository
                ReservationsViewModel(firestoreRepository, firebaseUserID)
            }
        }
    }
}