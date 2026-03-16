package com.example.chatzar_android.feature.auth.profile

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
import com.example.chatzar_android.data.remote.api.MemberApi
import com.example.chatzar_android.data.repository.MemberRepository
import com.example.chatzar_android.databinding.MemberFragmentProfileBinding
import kotlinx.coroutines.launch

class MyPageFragment : Fragment() {
    private var _binding: MemberFragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var tokenManager: TokenManager
    private lateinit var viewModel: MyPageViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        tokenManager = TokenManager(requireContext())
        _binding = MemberFragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupListeners()
        observeState()

        // 내 정보 불러오기
        val myId = tokenManager.getMemberId()
        if (myId != -1L) {
            viewModel.getMember(myId)
        }
    }

    private fun setupViewModel() {
        val api = ApiClient.retrofit.create(MemberApi::class.java)
        val repository = MemberRepository(api)
        val factory = MyPageViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[MyPageViewModel::class.java]
    }

    private fun setupListeners() {
        binding.btnMemberProfileBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnMemberEdit.setOnClickListener {
            findNavController().navigate(R.id.action_myPage_to_profileEdit)
        }

        binding.btnMatchPreference.setOnClickListener {
            findNavController().navigate(R.id.action_chatList_to_matchPreference)
        }

        binding.btnLogout.setOnClickListener {
            logout()
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect { state ->
                when (state) {
                    is MyPageUiState.Loading -> {
                        binding.pbProfileLoading.visibility = View.VISIBLE
                    }
                    is MyPageUiState.Success -> {
                        binding.pbProfileLoading.visibility = View.GONE
                        binding.tvMemberNickname.text = state.member.nickname
                        binding.tvMemberEmail.text = state.member.email
                    }
                    is MyPageUiState.Error -> {
                        binding.pbProfileLoading.visibility = View.GONE
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun logout() {
        tokenManager.clear()
        Toast.makeText(requireContext(), "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
        findNavController().navigate(R.id.loginFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
