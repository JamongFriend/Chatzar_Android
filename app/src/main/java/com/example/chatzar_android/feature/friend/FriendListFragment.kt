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
import com.example.chatzar_android.R
import com.example.chatzar_android.core.network.ApiClient
import com.example.chatzar_android.data.remote.api.FriendshipApi
import com.example.chatzar_android.data.repository.FriendshipRepository
import com.example.chatzar_android.databinding.FriendFragmentListBinding
import kotlinx.coroutines.launch

class FriendListFragment : Fragment() {
    private var _binding: FriendFragmentListBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: FriendViewModel
    private lateinit var adapter: FriendListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FriendFragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupRecyclerView()
        setupListeners()
        observeState()

        viewModel.fetchFriendData()
    }

    private fun setupViewModel() {
        val api = ApiClient.retrofit.create(FriendshipApi::class.java)
        val repository = FriendshipRepository(api)
        val factory = FriendViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[FriendViewModel::class.java]
    }

    private fun setupRecyclerView() {
        adapter = FriendListAdapter(
            onAcceptClick = { friendshipId ->
                viewModel.acceptRequest(friendshipId)
            },
            onFriendClick = { friend ->
                Toast.makeText(requireContext(), "${friend.nickname}님과 대화하기", Toast.LENGTH_SHORT).show()
            }
        )
        binding.rvFriendList.adapter = adapter
    }

    private fun setupListeners() {
        binding.btnFriendListBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.fabAddFriend.setOnClickListener {
            // 현재 사용 가능한 FriendRequestsFragment로 이동하거나, 나중에 검색 화면 개발 시 수정
            findNavController().navigate(R.id.action_friendList_to_friendRequests)
        }
        
        binding.btnDoSearch.setOnClickListener {
            val query = binding.etFriendSearch.text.toString()
            if (query.isNotBlank()) {
                Toast.makeText(requireContext(), "'$query' 검색 (준비 중)", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect { state ->
                when (state) {
                    is FriendUiState.Loading -> {
                        // 로딩 표시 생략
                    }
                    is FriendUiState.Success -> {
                        val listItems = mutableListOf<FriendListItem>()
                        
                        if (state.pendingRequests.isNotEmpty()) {
                            listItems.add(FriendListItem.Header("대기 중인 요청 (${state.pendingRequests.size})"))
                            listItems.addAll(state.pendingRequests.map { FriendListItem.Request(it) })
                        }
                        
                        listItems.add(FriendListItem.Header("내 친구 (${state.friends.size})"))
                        listItems.addAll(state.friends.map { FriendListItem.Friend(it) })
                        
                        adapter.submitList(listItems)
                    }
                    is FriendUiState.Error -> {
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
