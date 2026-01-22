package com.example.chatzar_android.feature.auth.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.chatzar_android.core.network.ApiClient
import com.example.chatzar_android.data.remote.api.AuthApi
import com.example.chatzar_android.data.remote.dto.SignupRequest
import com.example.chatzar_android.data.repository.AuthRepository
import com.example.chatzar_android.databinding.FragmentSignupBinding

class SignupFragment : Fragment() {
    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: SignupViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val authApi = ApiClient.retrofit.create(AuthApi::class.java)
        val repo = AuthRepository(authApi)
        val factory = SignupViewModelFactory(repo)
        viewModel = ViewModelProvider(this, factory)[SignupViewModel::class.java]

        binding.btnSignupSubmit.setOnClickListener {
            val email = binding.etSignupEmail.text.toString()
            val nickname = binding.etSignupNickname.text.toString()
            val password = binding.etSignupPassword.text.toString()

            val request = SignupRequest(email, nickname, password)
            viewModel.signup(request)

            // TODO: ViewModel을 통해 회원가입 API 호출 로직 구현
            Toast.makeText(requireContext(), "회원가입 요청: $nickname", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
