package com.example.chatzar_android.feature.auth.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.chatzar_android.core.network.ApiClient
import com.example.chatzar_android.data.remote.api.AuthApi
import com.example.chatzar_android.data.remote.dto.SignupRequest
import com.example.chatzar_android.data.repository.AuthRepository
import com.example.chatzar_android.databinding.AuthFragmentSignupBinding
import kotlinx.coroutines.launch

class SignupFragment : Fragment() {
    private var _binding: AuthFragmentSignupBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: SignupViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AuthFragmentSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val authApi = ApiClient.retrofit.create(AuthApi::class.java)
        val repo = AuthRepository(authApi)
        val factory = SignupViewModelFactory(repo)
        viewModel = ViewModelProvider(this, factory)[SignupViewModel::class.java]

        setupListeners()
        observeState()
    }

    private fun setupListeners() {
        binding.btnSignupBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnSignupSubmit.setOnClickListener {
            val email = binding.etSignupEmail.text.toString()
            val nickname = binding.etSignupNickname.text.toString()
            val password = binding.etSignupPassword.text.toString()

            val request = SignupRequest(email, nickname, password)
            viewModel.signup(request)
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect { state ->
                when (state) {
                    is SignupUiState.Idle -> Unit
                    is SignupUiState.Loading -> {
                        Toast.makeText(requireContext(), "회원가입 중...", Toast.LENGTH_SHORT).show()
                    }
                    is SignupUiState.Success -> {
                        Toast.makeText(requireContext(), "회원가입 성공! 로그인해주세요.", Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()
                    }
                    is SignupUiState.Error -> {
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
