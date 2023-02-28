package com.example.bookingapp.views.account

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.fragment.app.viewModels
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import com.example.bookingapp.R
import com.example.bookingapp.databinding.FragmentAccountAccountBinding
import com.example.bookingapp.viewmodels.AuthViewModel
import kotlinx.coroutines.launch

class AccountAccountFragment : Fragment() {
    private var _binding: FragmentAccountAccountBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    private val viewModel: AuthViewModel by viewModels { AuthViewModel.Factory }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.accountToolbar.inflateMenu(R.menu.account_menu)
        binding.accountToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                //Todo: add edit account function
                R.id.edit_account -> {
                    Log.d("Account Menu", "on edit account")
                    true
                }
                R.id.sign_out -> {
                    viewModel.signOut()
                    true
                }
                else -> false
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userData.collect {
                    if (it == null) {
                        binding.username.text = ""
                        binding.userEmail.text = ""
                        val action = AccountAccountFragmentDirections.toLogIn()
                        view.findNavController().navigate(action)
                    } else {
                        Log.d("AccountAccountFragment", "On account true")
                        binding.username.text = it.displayName
                        binding.userEmail.text = it.email
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}