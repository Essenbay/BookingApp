package com.example.bookingapp.views.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.bookingapp.data.models.Establishment
import com.example.bookingapp.databinding.FragmentHomeAddEstablishmentBinding
import com.example.bookingapp.databinding.FragmentHomeEstablishmentDetailBinding
import com.example.bookingapp.util.FirebaseResult
import com.example.bookingapp.viewmodels.HomeEstablishmentDetailViewModel
import com.example.bookingapp.viewmodels.HomeEstablishmentDetailViewModelFactory
import com.example.bookingapp.viewmodels.HomeViewModel
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch

class EstablishmentDetail : Fragment() {
    private var _binding: FragmentHomeEstablishmentDetailBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }
    private val args:
    private val viewModel: HomeEstablishmentDetailViewModel by viewModels { HomeEstablishmentDetailViewModel.getFactory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeEstablishmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    private fun createReservation(
        establishment: Establishment,
        tableID: Int,
        date: Timestamp
    ) = viewLifecycleOwner.lifecycleScope.launch {
        when (val result = viewModel.createReservation(establishment, tableID, date)) {
            is FirebaseResult.Success -> {
                Toast.makeText(context, "The reservation was created", Toast.LENGTH_LONG).show()
            }
            is FirebaseResult.Error -> {
                Toast.makeText(context, result.exception.message, Toast.LENGTH_LONG).show()
            }
        }
    }
}