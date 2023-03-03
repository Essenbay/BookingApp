package com.example.bookingapp.views.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView.OnQueryTextListener
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.bookingapp.databinding.FragmentHomeBinding
import com.example.bookingapp.util.FirebaseResult
import com.example.bookingapp.viewmodels.HomeViewModel
import com.example.bookingapp.viewmodels.SearchResult
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }
    private val viewModel: HomeViewModel by viewModels { HomeViewModel.Factory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.searchEstablishmentsView.setOnQueryTextListener(object : OnQueryTextListener {
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
                viewModel.establishments.collect {
                    when (it) {
                        is FirebaseResult.Success -> {
                            var establishmentsStr = "List: "
                            it.data.forEach { e -> establishmentsStr += e.name + '\n' }
                            binding.establishments.text = establishmentsStr
                        }
                        is FirebaseResult.Error -> Toast.makeText(
                            context,
                            "Could not load establishments",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    private fun onSearch(query: String?) = viewLifecycleOwner.lifecycleScope.launch {
        if (query == null || query.isBlank()) viewModel.getAllEstablishments()
        else {
            when (val result = viewModel.searchEstablishments(query)) {
                //The list is changed in view layer
                is SearchResult.Success -> {}
                is SearchResult.Empty -> {binding.emptyResultMsg.visibility = View.VISIBLE}
                is SearchResult.Error -> {
                    //Show error
                    view?.let { Snackbar.make(it, "Something went wrong...", Snackbar.LENGTH_LONG) }
                    Log.d("HomeFragment", result.exception.toString())
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}