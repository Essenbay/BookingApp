package com.example.bookingapp.views.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.example.bookingapp.databinding.FragmentAccountSignUpBinding
import com.example.bookingapp.util.FirebaseResult
import com.example.bookingapp.util.checkLoginField
import com.example.bookingapp.util.checkPasswordField
import com.example.bookingapp.util.checkPasswordMatching
import com.example.bookingapp.viewmodels.AuthViewModel
import kotlinx.coroutines.flow.update

class AccountSignUpFragment : Fragment() {
    private var _binding: FragmentAccountSignUpBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }
    private val viewModel: AuthViewModel by viewModels { AuthViewModel.Factory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            editSignupEmail.doOnTextChanged { text, _, _, _ ->
                viewModel.userInputState.update {
                    it.copy(emailInput = text.toString())
                }
            }
            editSingupPassword.doOnTextChanged { text, _, _, _ ->
                viewModel.userInputState.update {
                    it.copy(passwordInput = text.toString())
                }
            }
            editSigUpConfirmPassword.doOnTextChanged { text, _, _, _ ->
                viewModel.userInputState.update {
                    it.copy(confirmPasswordInput = text.toString())
                }
            }
            signUpBtn.setOnClickListener {
                val email = editSignupEmail.text.toString()
                val password = editSingupPassword.text.toString()
                val confirmPassword = editSigUpConfirmPassword.text.toString()

                if (email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
                    Toast.makeText(context, "Fields must not be blank", Toast.LENGTH_LONG).show()
                } else if (!checkLoginField(email)) {
                    Toast.makeText(context, "Email is not valid", Toast.LENGTH_LONG).show()
                } else if (!checkPasswordMatching(password, confirmPassword)) {
                    Toast.makeText(context, "Passwords are not matching", Toast.LENGTH_LONG).show()
                } else if (!checkPasswordField(password)) {
                    Toast.makeText(
                        context,
                        "Password must have at least 6 characters",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    register(email, password, view)
                }
            }
            toLogInLink.setOnClickListener {
                viewModel.userInputState.update {
                    it.copy(passwordInput = "", confirmPasswordInput = "")
                }
                view.findNavController().navigateUp()
            }
        }
    }

    private fun register(email: String, password: String, view: View) {
        viewModel.register(email, password).observe(viewLifecycleOwner) { firebaseResult ->
            firebaseResult?.let { result ->
                when (result) {
                    is FirebaseResult.Success -> {
                        Toast.makeText(context, "Successfully registered", Toast.LENGTH_LONG).show()
                        val action = AccountSignUpFragmentDirections.toAccount()
                        viewModel.userInputState.update {
                            it.copy(passwordInput = "", emailInput = "")
                        }
                        view.findNavController().navigate(action)
                    }
                    is FirebaseResult.Error -> {
                        Toast.makeText(
                            context,
                            result.exception.localizedMessage ?: "Unknown error", Toast.LENGTH_LONG
                        ).show()
                    }
                    is FirebaseResult.Loading -> {
                        Toast.makeText(context, "Loading...", Toast.LENGTH_LONG).show()
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