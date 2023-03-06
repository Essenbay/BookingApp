package com.example.bookingapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bookingapp.BookingApplication
import com.example.bookingapp.data.models.Establishment
import com.example.bookingapp.data.repositories.FirestoreRepository
import com.example.bookingapp.util.FirebaseResult
import com.example.bookingapp.util.SearchResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class HomeViewModel(private val firestoreRepository: FirestoreRepository) :
    ViewModel() {
    private var _firestoreEstablishments: List<Establishment> = emptyList()
    private var _filteredEstablishments: MutableStateFlow<SearchResult> =
        MutableStateFlow(SearchResult.Loading)
    val filteredEstablishments = _filteredEstablishments.asStateFlow()


    init {
        viewModelScope.launch {
            when (val result = firestoreRepository.getEstablishments()) {
                is FirebaseResult.Success -> {
                    _firestoreEstablishments = result.data
                    searchEstablishments("")
                }
                is FirebaseResult.Error -> _filteredEstablishments.update {
                    SearchResult.Error(result.exception)
                }
            }
        }
    }

    suspend fun searchEstablishments(query: String?) {
        _filteredEstablishments.update { startSearchEstablishments(query) }
    }

    private suspend fun startSearchEstablishments(query: String?): SearchResult =
        suspendCoroutine { cont ->
            if (query == null || query.isBlank()) cont.resume(
                SearchResult.Success(_firestoreEstablishments)
            )
            else {
                val resultList = mutableListOf<Establishment>()
                for (e in _firestoreEstablishments) {
                    if (e.name.lowercase().contains(query.lowercase())) resultList.add(e)
                }
                if (resultList.isEmpty()) cont.resume(SearchResult.Empty)
                else cont.resume(SearchResult.Success(resultList))
            }
        }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as BookingApplication)
                val firestoreRepository = application.container.firestoreRepository
                HomeViewModel(firestoreRepository)
            }
        }
    }
}