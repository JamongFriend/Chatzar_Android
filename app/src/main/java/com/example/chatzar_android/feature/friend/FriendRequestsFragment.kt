package com.example.chatzar_android.feature.friend

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatzar_android.core.network.ApiClient
import com.example.chatzar_android.data.remote.api.FriendshipApi
import com.example.chatzar_android.data.repository.FriendshipRepository
import com.example.chatzar_android.databinding.FriendFragmentRequestsBinding
import kotlinx.coroutines.launch

class FriendRequestsFragment : Fragment() {
    private var _binding: FriendFragmentRequestsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: FriendRequestsViewModel
    private lateinit var adapter: FriendRequestsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FriendFragmentRequestsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupRecyclerView()
        setupListeners()
        observeState()

        viewModel.getPendingRequests()
    }

    private fun setupViewModel() {
        val api = ApiClient.retrofit.create(FriendshipApi::class.java)
        val repository = FriendshipRepository(api)
        val factory = FriendRequestsViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[FriendRequestsViewModel::class.java]
    }

    private fun setupRecyclerView() {
        adapter = FriendRequestsAdapter(
            onAcceptClick = { request ->
                viewModel.acceptRequest(request.friendshipId)
            },
            onRejectClick = { request ->
                Toast.makeText(requireContext(), "거절 기능은 준비 중입니다.", Toast.LENGTH_SHORT).show()
            }
        )
        binding.rvFriendRequests.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFriendRequests.adapter = adapter
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect { state ->
                when (state) {
                    is FriendRequestsUiState.Loading -> {
                        // 로딩 표시 생략 (RecyclerView가 대신 보여줌)
                    }
                    is FriendRequestsUiState.Success -> {
                        if (state.requests.isEmpty()) {
                            binding.tvEmptyNotice.visibility = View.VISIBLE
                            binding.rvFriendRequests.visibility = View.GONE
                        } else {
                            binding.tvEmptyNotice.visibility = View.GONE
                            binding.rvFriendRequests.visibility = View.VISIBLE
                            adapter.submitList(state.requests)
                        }
                    }
                    is FriendRequestsUiState.AcceptSuccess -> {
                        Toast.makeText(requireContext(), "친구 신청을 수락했습니다!", Toast.LENGTH_SHORT).show()
                    }
                    is FriendRequestsUiState.Error -> {
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
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
