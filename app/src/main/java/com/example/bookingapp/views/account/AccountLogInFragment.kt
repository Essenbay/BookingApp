package com.example.bookingapp.views.account

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.bookingapp.databinding.FragmentAccountLogInBinding
import com.example.bookingapp.util.FirebaseResult
import com.example.bookingapp.util.checkLoginField
import com.example.bookingapp.util.checkPasswordField
import com.example.bookingapp.viewmodels.AccountViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.update

class AccountLogInFragment : Fragment() {
    private var _binding: FragmentAccountLogInBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }
    private val viewModel: AccountViewModel by viewModels { AccountViewModel.Factory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountLogInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            editLoginEmail.doOnTextChanged { text, _, _, _ ->
                viewModel.userInputState.update {
                    it.copy(emailInput = text.toString())
                }
            }
            editLoginPassword.doOnTextChanged { text, _, _, _ ->
                viewModel.userInputState.update {
                    it.copy(passwordInput = text.toString())
                }
            }

            loginBtn.setOnClickListener {
                val email = editLoginEmail.text.toString()
                val password = editLoginPassword.text.toString()
                if (email.isBlank() || password.isBlank()) {
                    Toast.makeText(context, "Fields must not be blank", Toast.LENGTH_LONG).show()
                } else if (!checkLoginField(email)) {
                    Toast.makeText(context, "Email is not valid", Toast.LENGTH_LONG).show()
                } else if (!checkPasswordField(password)) {
                    Toast.makeText(
                        context,
                        "Password must have at least 6 characters",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    binding.progressCircular.visibility = View.VISIBLE
                    login(email, password, view).start()
                }
            }
            toSignupBtn.setOnClickListener {
                Log.d("Login fragment", "on register")
                val action = AccountLogInFragmentDirections.logInToSignUp()
                viewModel.userInputState.update {
                    it.copy(passwordInput = "", emailInput = "")
                }
                view.findNavController().navigate(action)
            }
        }
    }

    private fun login(email: String, password: String, view: View) = viewLifecycleOwner.lifecycleScope.launch {
        binding.progressCircular.visibility = View.VISIBLE
        when(val result = viewModel.login(email, password)){
            is FirebaseResult.Success -> {
                Toast.makeText(context, "Successfully logged in", Toast.LENGTH_LONG).show()
                view.findNavController().popBackStack()
            }
            is FirebaseResult.Error -> {
                Toast.makeText(
                    context,
                    result.exception.localizedMessage ?: "Unknown error", Toast.LENGTH_LONG
                ).show()
                binding.progressCircular.visibility = View.INVISIBLE
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}