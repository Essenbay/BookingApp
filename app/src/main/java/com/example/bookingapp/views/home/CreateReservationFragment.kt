package com.example.bookingapp.views.home

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.bookingapp.data.models.Reservation
import com.example.bookingapp.databinding.FragmentReservationCreateBinding
import java.text.SimpleDateFormat
import java.util.*

const val KEY = "CREATE_RESERVATION_RESULT"

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
        binding.establishmentName.text = args.establishment.name

        binding.tableId.setOnClickListener {
            val items: Array<CharSequence> = (1..args.establishment.tableNumber).map {
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

        binding.fromDateBtn.setOnClickListener {
            pickFromDateTime()
        }

        binding.toDateBtn.setOnClickListener {
            pickToDateTime()
        }

        binding.cancelBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.createReservationBtn.setOnClickListener {
            findNavController().navigateUp()
//            findNavController().previousBackStackEntry?.savedStateHandle?.set(
//                KEY,
//                listOf(tableID, dateTime)
//            )
        }
    }

    private fun pickFromDateTime() {
        DatePickerDialog(
            requireContext(), { _, year, month, day ->
                TimePickerDialog(
                    requireContext(), { _, hour, minute ->
                        val pickedDateTime = Calendar.getInstance()
                        pickedDateTime.set(year, month, day, hour, minute)
                        binding.fromDateBtn.text =
                            SimpleDateFormat(
                                "d MMMM, yyyy, h:mm a",
                                Locale.getDefault()
                            ).format(pickedDateTime.time)
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
                            SimpleDateFormat(
                                "d MMMM, yyyy, h:mm a",
                                Locale.getDefault()
                            ).format(pickedDateTime.time)
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

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

}