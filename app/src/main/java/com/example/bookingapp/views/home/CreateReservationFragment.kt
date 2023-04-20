package com.example.bookingapp.views.home

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.example.bookingapp.R
import com.example.bookingapp.data.models.Establishment
import com.example.bookingapp.databinding.FragmentReservationCreateBinding
import com.example.bookingapp.util.FirebaseResult
import com.example.bookingapp.util.formatDate
import com.example.bookingapp.viewmodels.HomeEstablishmentDetailViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch
import java.util.*

class CreateReservationFragment : Fragment() {
    private val args: CreateReservationFragmentArgs by navArgs()
    private var _binding: FragmentReservationCreateBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }
    private var fromDateTime: Calendar = Calendar.getInstance()
    private var toDateTime: Calendar = Calendar.getInstance()
    private var tableId: Int = 1
    private val viewModel: HomeEstablishmentDetailViewModel by navGraphViewModels(R.id.home_navigation) {
        HomeEstablishmentDetailViewModel.getFactory(
            args.establishment.establishmentId
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReservationCreateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Initialize reservation
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.establishment.collect { result ->
                    when (result) {
                        is FirebaseResult.Success -> with(result.data) {
                            binding.progressBar.visibility = View.INVISIBLE

                            binding.establishmentName.text = this.name
                            binding.tableId.setOnClickListener {
                                val items: Array<CharSequence> = (1..this.tableNumber).map {
                                    it.toString()
                                }.toTypedArray()
                                var chosenId = tableId
                                AlertDialog.Builder(context)
                                    .setTitle("Choose table")
                                    .setSingleChoiceItems(items, tableId - 1) { _, which ->
                                        chosenId = which + 1
                                    }
                                    .setItems(items) { _, which ->
                                        chosenId = which + 1
                                    }
                                    .setNegativeButton("Cancel") { _, _ ->

                                    }
                                    .setPositiveButton("Save") { _, _ ->
                                        binding.tableId.text = chosenId.toString()
                                        tableId = chosenId
                                    }.show()
                            }
                            binding.fromDateBtn.text =
                                formatDate(fromDateTime.time)
                            binding.fromDateBtn.setOnClickListener {
                                pickFromDateTime()
                            }

                            binding.toDateBtn.text =
                                formatDate(toDateTime.time)
                            binding.toDateBtn.setOnClickListener {
                                pickToDateTime()
                            }

                            binding.cancelBtn.setOnClickListener {
                                findNavController().navigateUp()
                            }

                            binding.createReservationBtn.setOnClickListener {
                                if (checkFields()) {
                                    createReservation(this.establishmentId)
                                }
                            }
                        }
                        is FirebaseResult.Error -> {
                            binding.progressBar.visibility = View.INVISIBLE

                            Snackbar.make(view, "${result.exception}", Snackbar.LENGTH_LONG).show()
                            findNavController().navigateUp()
                        }
                        else -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }
                    }
                }
            }


        }
    }

    private fun pickFromDateTime() {
        DatePickerDialog(
            requireContext(), { _, year, month, day ->
                TimePickerDialog(
                    requireContext(), { _, hour, minute ->
                        val pickedDateTime = Calendar.getInstance()
                        pickedDateTime.set(year, month, day, hour, minute)
                        binding.fromDateBtn.text = formatDate(pickedDateTime.time)
                        fromDateTime = pickedDateTime
                    },
                    fromDateTime.get(Calendar.HOUR_OF_DAY),
                    fromDateTime.get(Calendar.MINUTE),
                    false
                ).show()
            },
            fromDateTime.get(Calendar.YEAR),
            fromDateTime.get(Calendar.MONTH),
            fromDateTime.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun pickToDateTime() {
        DatePickerDialog(
            requireContext(), { _, year, month, day ->
                TimePickerDialog(
                    requireContext(), { _, hour, minute ->
                        val pickedDateTime = Calendar.getInstance()
                        pickedDateTime.set(year, month, day, hour, minute)
                        binding.toDateBtn.text =
                            formatDate(pickedDateTime.time)
                        toDateTime = pickedDateTime
                    },
                    toDateTime.get(Calendar.HOUR_OF_DAY),
                    toDateTime.get(Calendar.MINUTE),
                    false
                ).show()
            },
            toDateTime.get(Calendar.YEAR),
            toDateTime.get(Calendar.MONTH),
            toDateTime.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun createReservation(establishmentId: String) =
        viewLifecycleOwner.lifecycleScope.launch {
            when (val result = viewModel.createReservation(
                establishmentId,
                tableId,
                Timestamp(fromDateTime.time),
                Timestamp(toDateTime.time)
            )) {
                is FirebaseResult.Success -> {
                    Snackbar.make(
                        requireView(),
                        "Reservation was created!",
                        Snackbar.LENGTH_LONG
                    ).show()
                    findNavController().navigateUp()
                }
                is FirebaseResult.Error -> {
                    Snackbar.make(
                        requireView(),
                        result.exception.message ?: "Error",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        }

    private fun checkFields(): Boolean {
        return if (fromDateTime >= toDateTime) {
            Snackbar.make(
                requireView(),
                "No free reservations for such time period...",
                Snackbar.LENGTH_SHORT
            ).show()
            false
        } else {
            true
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

}