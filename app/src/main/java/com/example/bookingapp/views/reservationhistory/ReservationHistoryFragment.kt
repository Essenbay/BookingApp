package com.example.bookingapp.views.reservationhistory

import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.example.bookingapp.AuthNavigationDirections
import com.example.bookingapp.R
import com.example.bookingapp.data.models.Reservation
import com.example.bookingapp.databinding.FragmentReservationHistoryBinding
import com.example.bookingapp.util.SearchResult
import com.example.bookingapp.util.UserNotSignedIn
import com.example.bookingapp.viewmodels.ReservationsViewModel
import com.example.bookingapp.views.home.HomeFragmentDirections
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ReservationHistoryFragment : Fragment() {
    private var _binding: FragmentReservationHistoryBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }
    private val viewModel: ReservationsViewModel by viewModels { ReservationsViewModel.Factory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReservationHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.user.collect {
                    if (it == null) {
                        Snackbar.make(view, "You are not signed in!", Snackbar.LENGTH_LONG).show()
                        val action = ReservationHistoryFragmentDirections.toAccount()
                        findNavController().navigate(action)
                    } else {
                        Log.d("ReservationHistory", "Signed in!!!!")
                    }
                }
            }
        }

        onSearch(null)

        binding.reservationsToolbar.inflateMenu(R.menu.reservations_menu)
        binding.reservationsToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.update_reservation_list -> {
                    viewModel.getReservations()
                    true
                }
                else -> false
            }
        }
        val searchItem = binding.reservationsToolbar.menu.findItem(R.id.menu_item_search)
        val searchView = searchItem.actionView as? SearchView

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                onSearch(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                onSearch(newText)
                return false
            }
        })

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.filteredReservations.collect {
                    handleFilteredReservations(it, view)
                }
            }
        }
    }

    private fun onSearch(query: String?) = viewLifecycleOwner.lifecycleScope.launch {
        viewModel.searchReservations(query)
    }


    //Todo: Progress bar is not showing
    private fun handleFilteredReservations(result: SearchResult<List<Reservation>>, view: View) {
        when (result) {
            is SearchResult.Success -> {
                var resultStr = "Reservations: \n"
                for (e in result.result) {
                    val dateStr = DateFormat.format("dd.MM.yyyy HH:mm", e.dateTime.toDate())
                    resultStr += "Establishment: ${e.establishment.name}, table #${e.tableID}, at: $dateStr\n"
                }
                binding.reservations.text = resultStr
                binding.emptyResultMsg.visibility = View.INVISIBLE
            }
            is SearchResult.Empty -> {
                binding.reservations.text = ""
                binding.emptyResultMsg.visibility = View.VISIBLE
                binding.progressBar.visibility = View.INVISIBLE
            }
            is SearchResult.Error -> {
                if (result.exception is UserNotSignedIn) {
                    Snackbar.make(view, "You are not signed in!", Snackbar.LENGTH_LONG).show()
                    val action = ReservationHistoryFragmentDirections.toAccount()
                    findNavController().navigate(action)
                } else {
                    binding.emptyResultMsg.visibility = View.INVISIBLE
                    binding.progressBar.visibility = View.INVISIBLE
                    Snackbar.make(view, "Something went wrong...", Snackbar.LENGTH_LONG).show()
                }
            }
            is SearchResult.Loading -> {
                binding.progressBar.visibility = View.VISIBLE
                binding.reservations.visibility = View.INVISIBLE
                binding.emptyResultMsg.visibility = View.INVISIBLE
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}