package com.example.bookingapp.views.reservationhistory

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.bookingapp.R
import com.example.bookingapp.data.models.Establishment
import com.example.bookingapp.data.models.Reservation
import com.example.bookingapp.databinding.FragmentReservationHistoryBinding
import com.example.bookingapp.util.*
import com.example.bookingapp.viewmodels.ReservationsViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

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


    private fun handleFilteredReservations(
        result: SearchResult<Map<Reservation, Establishment>>,
        view: View
    ) {
        when (result) {
            is SearchResult.Success -> {
                var resultStr = "Reservations: \n"
                for (r in result.result) {
                    try {
                        resultStr += "Establishment: ${r.value.name}, table #${r.key.tableID}, ${
                            formatDate(
                                r.key.fromDate.toDate()
                            )
                        } - ${formatDate(r.key.toDate.toDate())}\n"
                    } catch (e: Exception) {
                        Snackbar.make(
                            view,
                            e.message ?: "An error occurred: $e",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                    binding.reservations.text = resultStr
                    binding.emptyResultMsg.visibility = View.INVISIBLE
                }
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
            else -> {
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