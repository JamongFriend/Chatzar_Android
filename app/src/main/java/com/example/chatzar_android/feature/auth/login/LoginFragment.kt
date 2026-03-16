package com.example.chatzar_android.feature.auth.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.chatzar_android.R
import com.example.chatzar_android.core.network.ApiClient
import com.example.chatzar_android.core.network.TokenManager
import com.example.chatzar_android.data.remote.api.AuthApi
import com.example.chatzar_android.data.repository.AuthRepository
import kotlinx.coroutines.launch

import com.example.chatzar_android.databinding.AuthFragmentLoginBinding

class LoginFragment : Fragment() {
    private var _binding: AuthFragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var vm: LoginViewModel
    private lateinit var tokenManager: TokenManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        tokenManager = TokenManager(requireContext())
        _binding = AuthFragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val authApi = ApiClient.retrofit.create(AuthApi::class.java)
        val repo = AuthRepository(authApi)
        vm = ViewModelProvider(this, LoginViewModelFactory(repo))[LoginViewModel::class.java]

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            vm.login(email, password)
        }

        binding.tvGoSignup.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signupFragment)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            vm.state.collect { state ->
                when (state) {
                    is LoginUiState.Idle -> Unit
                    is LoginUiState.Loading -> Toast.makeText(requireContext(), "로그인 중...", Toast.LENGTH_SHORT).show()
                    is LoginUiState.Success -> {
                        // 로그인 정보 저장
                        state.data.accessToken?.let { tokenManager.saveToken(it) }
                        tokenManager.saveMemberId(state.data.memberId)

                        Toast.makeText(requireContext(), "환영합니다!", Toast.LENGTH_SHORT).show()
                        
                        // 채팅 목록 화면으로 이동
                        findNavController().navigate(R.id.action_login_to_chatList)
                    }
                    is LoginUiState.Error -> Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}