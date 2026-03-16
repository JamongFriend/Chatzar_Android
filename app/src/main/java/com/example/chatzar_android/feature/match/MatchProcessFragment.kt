package com.example.chatzar_android.feature.match

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
import com.example.chatzar_android.data.remote.api.MatchApi
import com.example.chatzar_android.data.repository.MatchRepository
import com.example.chatzar_android.databinding.MatchFragmentMatchingProcessBinding
import kotlinx.coroutines.launch

class MatchProcessFragment : Fragment() {
    private var _binding: MatchFragmentMatchingProcessBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: MatchViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MatchFragmentMatchingProcessBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupListeners()
        observeState()

        viewModel.requestMatch()
    }

    private fun setupViewModel() {
        val api = ApiClient.retrofit.create(MatchApi::class.java)
        val repository = MatchRepository(api)
        val factory = MatchViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[MatchViewModel::class.java]
    }

    private fun setupListeners() {
        binding.btnCancelMatch.setOnClickListener {
            viewModel.cancelMatch()
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect { state ->
                when (state) {
                    is MatchUiState.Waiting -> {
                        binding.tvMatchingStatus.text = "최적의 상대를 찾는 중..."
                    }
                    is MatchUiState.Matched -> {
                        Toast.makeText(requireContext(), "${state.partnerNickname}님과 매칭되었습니다!", Toast.LENGTH_SHORT).show()

                        val bundle = Bundle().apply {
                            putString("roomId", state.chatRoomId.toString())
                        }
                        findNavController().navigate(R.id.action_matchProcessFragment_to_chatDetailFragment, bundle)
                    }
                    is MatchUiState.Canceled -> {
                        Toast.makeText(requireContext(), "매칭이 취소되었습니다.", Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()
                    }
                    is MatchUiState.Error -> {
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                        findNavController().popBackStack()
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
