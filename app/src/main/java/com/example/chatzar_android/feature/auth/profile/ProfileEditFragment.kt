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
import com.example.chatzar_android.core.network.ApiClient
import com.example.chatzar_android.core.network.TokenManager
import com.example.chatzar_android.data.remote.api.MemberApi
import com.example.chatzar_android.data.repository.MemberRepository
import com.example.chatzar_android.databinding.MemberFragmentProfileEditBinding
import kotlinx.coroutines.launch

class ProfileEditFragment : Fragment() {
    private var _binding: MemberFragmentProfileEditBinding? = null
    private val binding get() = _binding!!
    private lateinit var tokenManager: TokenManager
    private lateinit var viewModel: ProfileEditViewModel
    private var currentMemberId: Long = -1L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        tokenManager = TokenManager(requireContext())
        _binding = MemberFragmentProfileEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupListeners()
        observeState()

        currentMemberId = tokenManager.getMemberId()
        if (currentMemberId != -1L) {
            viewModel.getMember(currentMemberId)
        }
    }

    private fun setupViewModel() {
        val api = ApiClient.retrofit.create(MemberApi::class.java)
        val repository = MemberRepository(api)
        val factory = ProfileEditViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[ProfileEditViewModel::class.java]
    }

    private fun setupListeners() {
        binding.btnProfileEditBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.tvProfileEditSave.setOnClickListener {
            val newNickname = binding.etProfileEditNickname.text.toString().trim()
            if (newNickname.isNotEmpty()) {
                viewModel.updateMember(currentMemberId, newNickname)
            } else {
                Toast.makeText(requireContext(), "닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect { state ->
                when (state) {
                    is ProfileEditUiState.Loading -> {
                        binding.pbProfileEditLoading.visibility = View.VISIBLE
                    }
                    is ProfileEditUiState.LoadSuccess -> {
                        binding.pbProfileEditLoading.visibility = View.GONE
                        binding.etProfileEditNickname.setText(state.member.nickname)
                    }
                    is ProfileEditUiState.UpdateSuccess -> {
                        binding.pbProfileEditLoading.visibility = View.GONE
                        Toast.makeText(requireContext(), "프로필이 수정되었습니다.", Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()
                    }
                    is ProfileEditUiState.Error -> {
                        binding.pbProfileEditLoading.visibility = View.GONE
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
