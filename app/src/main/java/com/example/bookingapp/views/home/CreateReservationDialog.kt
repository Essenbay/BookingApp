package com.example.bookingapp.views.home

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.bookingapp.R
import com.example.bookingapp.viewmodels.HomeEstablishmentDetailViewModel

class CreateReservationDialog : DialogFragment() {
    private val args: CreateReservationDialogArgs by navArgs()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = layoutInflater.inflate(R.layout.fragment_create_reservation, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(view)
            .setTitle("Create Reservation")
            .setNegativeButton(view.findViewById(R.id.cancel_btn)) { _,_ ->
                findNavController().previousBackStackEntry?.savedStateHandle.set()
            }
        return super.onCreateDialog(savedInstanceState)
    }
}