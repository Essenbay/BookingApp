package com.example.bookingapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bookingapp.BookingApplication
import com.example.bookingapp.data.models.Establishment
import com.example.bookingapp.data.repositories.AccessUser
import com.example.bookingapp.data.repositories.EstablishmentsRepository
import com.example.bookingapp.util.FirebaseResult
import com.example.bookingapp.util.SearchResult
import com.example.bookingapp.util.UserNotSignedIn
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class HomeViewModel(
    private val establishmentsRepository: EstablishmentsRepository,
    private val accessUser: AccessUser
) :
    ViewModel() {
    private var _establishments: List<Establishment> = emptyList()
    private var _filteredEstablishments: MutableStateFlow<SearchResult<List<Establishment>>> =
        MutableStateFlow(SearchResult.Loading)
    val filteredEstablishments = _filteredEstablishments.asStateFlow()


    init {
        getEstablishments()
    }

    suspend fun searchEstablishments(query: String?) {
        val result = startSearchEstablishments(query)
        _filteredEstablishments.update { result }
    }

    private suspend fun startSearchEstablishments(query: String?): SearchResult<List<Establishment>> =
        suspendCoroutine { cont ->
            if (query == null || query.isBlank())
                cont.resume(SearchResult.Success(_establishments))
            else {
                val resultList = mutableListOf<Establishment>()
                for (e in _establishments) {
                    if (e.name.lowercase().contains(query.lowercase())) resultList.add(e)
                }
                if (resultList.isEmpty()) cont.resume(SearchResult.Empty)
                else cont.resume(SearchResult.Success(resultList))
            }
        }

    suspend fun addEstablishment(newEstablishment: Establishment): FirebaseResult<Boolean> =
        establishmentsRepository.createEstablishment(newEstablishment)

    fun getEstablishments() = viewModelScope.launch {
        when (val result = establishmentsRepository.getEstablishments()) {
            is FirebaseResult.Success -> {
                _establishments = result.data
                searchEstablishments("")
            }
            is FirebaseResult.Error -> _filteredEstablishments.update {
                SearchResult.Error(result.exception)
            }
        }
    }

    suspend fun createReservation(
        establishment: Establishment,
        tableID: Int,
        date: Timestamp
    ): FirebaseResult<Boolean> {
        val userUID = accessUser.user.value?.uid
        return if (userUID == null) FirebaseResult.Error(UserNotSignedIn())
        else {
            establishmentsRepository.createReservation(
                userUID,
                establishment,
                tableID,
                date
            )
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as BookingApplication)
                val firestoreRepository = application.container.storageRepository
                val accessUserIDRepository = application.container.userRepository
                HomeViewModel(firestoreRepository, accessUserIDRepository)
            }
        }
    }
}