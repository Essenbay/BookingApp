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
import com.example.bookingapp.AuthNavigationDirections
import com.example.bookingapp.databinding.FragmentEditUserPasswordBinding
import com.example.bookingapp.util.FirebaseResult
import com.example.bookingapp.util.checkLoginField
import com.example.bookingapp.util.checkPasswordField
import com.example.bookingapp.util.checkPasswordMatching
import com.example.bookingapp.viewmodels.AccountViewModel
import kotlinx.coroutines.launch

class EditUserPasswordFragment : Fragment() {
    private var _binding: FragmentEditUserPasswordBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    private val viewModel: AccountViewModel by viewModels { AccountViewModel.Factory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditUserPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.saveChanges.setOnClickListener {
            val currPassword = binding.editCurrPassword.text.toString()
            val newPassword = binding.editNewPassword.text.toString()
            val confirmNewPassword = binding.editConfirmPassword.text.toString()

            if (newPassword.isBlank() || currPassword.isBlank() || confirmNewPassword.isBlank()) {
                Toast.makeText(context, "Fields must not be blank", Toast.LENGTH_LONG).show()
            } else if (!checkPasswordMatching(newPassword, confirmNewPassword)) {
                Toast.makeText(context, "Passwords are not matching", Toast.LENGTH_LONG).show()
            } else if (!checkPasswordField(newPassword)) {
                Toast.makeText(context, "Email is not valid", Toast.LENGTH_LONG).show()
            } else {
                editUserPassword(currPassword, newPassword, view)
            }
        }
    }
    //Todo: Add "forgot password?" function
    private fun editUserPassword(currPassword: String, newPassword: String, view: View) =
        viewLifecycleOwner.lifecycleScope.launch {
            binding.progressBar.visibility = View.VISIBLE
            when (val result = viewModel.editUserPassword(currPassword, newPassword)) {
                is FirebaseResult.Success -> {
                    Toast.makeText(context, "Successfully changed user password", Toast.LENGTH_LONG)
                        .show()
                    val action = AuthNavigationDirections.toAccount()
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