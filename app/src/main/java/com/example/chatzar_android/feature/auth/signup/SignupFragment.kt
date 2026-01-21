package com.example.chatzar_android.feature.auth.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.chatzar_android.core.network.ApiClient
import com.example.chatzar_android.data.remote.api.AuthApi
import com.example.chatzar_android.data.repository.AuthRepository
import com.example.chatzar_android.databinding.FragmentSignupBinding

class SignupFragment : Fragment() {
    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // API 및 Repository 준비 (나중엔 Hilt 같은 DI를 쓰면 더 깔끔합니다)
        val authApi = ApiClient.retrofit.create(AuthApi::class.java)
        val repo = AuthRepository(authApi)

        binding.btnSignupSubmit.setOnClickListener {
            val email = binding.etSignupEmail.text.toString()
            val nickname = binding.etSignupNickname.text.toString()
            val password = binding.etSignupPassword.text.toString()

            // TODO: ViewModel을 통해 회원가입 API 호출 로직 구현
            // 성공 시 다시 로그인 화면으로 돌아가게 처리
            Toast.makeText(requireContext(), "회원가입 요청: $nickname", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack() // 이전 화면(로그인)으로 돌아가기
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
