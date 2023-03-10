package com.example.bookingapp.views.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.bookingapp.databinding.FragmentEditUserEmailBinding
import com.example.bookingapp.util.FirebaseResult
import com.example.bookingapp.util.checkLoginField
import com.example.bookingapp.util.checkPasswordField
import com.example.bookingapp.viewmodels.AccountViewModel
import kotlinx.coroutines.launch

class EditEmailFragment : Fragment() {
    private var _binding: FragmentEditUserEmailBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    private val viewModel: AccountViewModel by viewModels { AccountViewModel.Factory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditUserEmailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.saveChanges.setOnClickListener {
            val password = binding.editPassword.text.toString()
            val newEmail = binding.editNewEmail.text.toString()
            if (newEmail.isBlank() || password.isBlank()) {
                Toast.makeText(context, "Fields must not be blank", Toast.LENGTH_LONG).show()
            } else if (!checkLoginField(newEmail)) {
                Toast.makeText(context, "Email is not valid", Toast.LENGTH_LONG).show()
            } else {
                editUserEmail(password, newEmail, view)
            }
        }
    }

    private fun editUserEmail(password: String, newEmail: String, view: View) =
        viewLifecycleOwner.lifecycleScope.launch {
            binding.progressBar.visibility = View.VISIBLE
            when (val result = viewModel.editUserEmail(password, newEmail)) {
                is FirebaseResult.Success -> {
                    Toast.makeText(context, "Successfully changed user email", Toast.LENGTH_LONG)
                        .show()
                    val action = AccountSignUpFragmentDirections.toAccount()
                    view.findNavController().navigate(action)
                }
                is FirebaseResult.Error -> {
                    Toast.makeText(
                        context,
                        result.exception.localizedMessage ?: "Unknown error",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            binding.progressBar.visibility = View.INVISIBLE
        }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}