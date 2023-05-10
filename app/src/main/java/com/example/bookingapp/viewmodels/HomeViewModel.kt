package com.example.bookingapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookingapp.data.models.Establishment
import com.example.bookingapp.data.repositories.EstablishmentsRepository
import com.example.bookingapp.util.FirebaseResult
import com.example.bookingapp.util.SearchResult
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val establishmentsRepository: EstablishmentsRepository,
) :
    ViewModel() {
    private var _establishments: List<Establishment> = emptyList()
    private var _filteredEstablishments: MutableStateFlow<SearchResult<List<Establishment>>> =
        MutableStateFlow(SearchResult.Loading)
    val filteredEstablishments = _filteredEstablishments.asStateFlow()


    init {
        getEstablishments()
//        val newEstablishment =
//            Establishment("", "", "", Timestamp.now(), Timestamp.now(), "", 1)
//        viewModelScope.launch {
//            addEstablishment(newEstablishment)
//        }
    }

    suspend fun searchEstablishments(query: String?) {
        val result = startSearchEstablishments(query)
        _filteredEstablishments.update { result }
    }

    //Todo: Filter???

    private suspend fun startSearchEstablishments(query: String?): SearchResult<List<Establishment>> =
        //Todo: Add search by description
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

    suspend fun addEstablishment(newEstablishment: Establishment): Boolean =
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
}