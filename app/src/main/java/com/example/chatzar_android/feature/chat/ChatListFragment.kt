package com.example.chatzar_android.feature.chat

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
import com.example.chatzar_android.R
import com.example.chatzar_android.core.network.ApiClient
import com.example.chatzar_android.data.remote.api.ChatApi
import com.example.chatzar_android.data.repository.ChatRepository
import com.example.chatzar_android.databinding.ChatFragmentListBinding
import kotlinx.coroutines.launch

class ChatListFragment : Fragment() {
    private var _binding: ChatFragmentListBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ChatListViewModel
    private lateinit var adapter: ChatListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ChatFragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupRecyclerView()
        setupListeners() // 추가
        observeState()

        viewModel.getMyChatRooms()
    }

    private fun setupViewModel() {
        val api = ApiClient.retrofit.create(ChatApi::class.java)
        val repository = ChatRepository(api)
        val factory = ChatListViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[ChatListViewModel::class.java]
    }

    private fun setupListeners() {
        // 채팅방 추가(매칭 설정) 버튼 클릭 시 매칭 화면으로 이동
        binding.fabAddChat.setOnClickListener {
            findNavController().navigate(R.id.action_chatList_to_matchPreference)
        }
    }

    private fun setupRecyclerView() {
        adapter = ChatListAdapter { room ->
            val bundle = Bundle().apply {
                putString("roomId", room.roomId.toString())
            }
            findNavController().navigate(R.id.action_chatList_to_chatDetail, bundle)
        }
        binding.rvChatList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = this@ChatListFragment.adapter
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect { state ->
                when (state) {
                    is ChatListUiState.Loading -> {
                        binding.pbChatList.visibility = View.VISIBLE
                    }
                    is ChatListUiState.Success -> {
                        binding.pbChatList.visibility = View.GONE
                        adapter.submitList(state.rooms)
                    }
                    is ChatListUiState.Error -> {
                        binding.pbChatList.visibility = View.GONE
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
