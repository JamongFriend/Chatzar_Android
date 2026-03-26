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
import com.example.chatzar_android.core.network.ApiClient
import com.example.chatzar_android.data.remote.api.ChatApi
import com.example.chatzar_android.data.remote.api.FriendshipApi
import com.example.chatzar_android.data.remote.api.MessageApi
import com.example.chatzar_android.data.repository.ChatRepository
import com.example.chatzar_android.data.repository.FriendshipRepository
import com.example.chatzar_android.data.repository.MessageRepository
import com.example.chatzar_android.databinding.ChatFragmentDetailBinding
import kotlinx.coroutines.launch

import com.example.chatzar_android.core.network.TokenManager

class ChatDetailFragment : Fragment() {
    private var _binding: ChatFragmentDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ChatDetailViewModel
    private lateinit var adapter: ChatDetailAdapter
    private lateinit var tokenManager: TokenManager
    private var roomId: Long = -1
    private var otherMemberId: Long = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        tokenManager = TokenManager(requireContext())
        _binding = ChatFragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // roomId를 String 또는 Long으로 유연하게 받기
        val roomIdRaw = arguments?.get("roomId")
        roomId = when (roomIdRaw) {
            is String -> roomIdRaw.toLongOrNull() ?: -1L
            is Long -> roomIdRaw
            else -> -1L
        }

        android.util.Log.d("ChatDetailFragment", "Received roomId: $roomId")

        if (roomId == -1L) {
            Toast.makeText(requireContext(), "잘못된 채팅방 접근입니다.", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
            return
        }


        setupViewModel()
        setupRecyclerView()
        setupListeners()
        observeState()

        viewModel.getChatRoom(roomId)
        viewModel.getMessages(roomId)

        val wsUrl = "ws://10.0.2.2:8080/ws"
        val token = tokenManager.getToken()
        viewModel.connectAndSubscribe(roomId, wsUrl, token)
    }

    private fun setupViewModel() {
        val messageApi = ApiClient.retrofit.create(MessageApi::class.java)
        val chatApi = ApiClient.retrofit.create(ChatApi::class.java)
        val friendshipApi = ApiClient.retrofit.create(FriendshipApi::class.java)
        
        val messageRepository = MessageRepository(messageApi)
        val chatRepository = ChatRepository(chatApi)
        val friendshipRepository = FriendshipRepository(friendshipApi)
        
        val factory = ChatDetailViewModelFactory(messageRepository, chatRepository, friendshipRepository)
        viewModel = ViewModelProvider(this, factory)[ChatDetailViewModel::class.java]
    }

    private fun setupRecyclerView() {
        val myId = tokenManager.getMemberId()
        adapter = ChatDetailAdapter(myMemberId = myId) 
        binding.rvChatMessages.apply {
            layoutManager = LinearLayoutManager(requireContext()).apply {
                stackFromEnd = true
            }
            this.adapter = this@ChatDetailFragment.adapter
        }
    }

    private fun setupListeners() {
        binding.btnChatBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnSend.setOnClickListener {
            val content = binding.etMessage.text.toString()
            if (content.isNotBlank()) {
                viewModel.sendMessage(roomId, content)
                binding.etMessage.text.clear()
            }
        }

        binding.btnRequestFriend.setOnClickListener {
            if (otherMemberId != -1L) {
                viewModel.sendFriendRequest(otherMemberId)
            }
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect { state ->
                when (state) {
                    is ChatDetailUiState.RoomStatusSuccess -> {
                        otherMemberId = state.room.otherMemberId
                        binding.tvChatRoomName.text = state.room.otherNickname
                        updateLockState(state.room.status)
                    }
                    is ChatDetailUiState.HistorySuccess -> {
                        adapter.submitList(state.messages)
                        binding.rvChatMessages.post {
                            if (adapter.itemCount > 0) {
                                binding.rvChatMessages.scrollToPosition(adapter.itemCount - 1)
                            }
                        }
                    }
                    is ChatDetailUiState.NewMessage -> {
                        adapter.addMessage(state.message)
                        binding.rvChatMessages.post {
                            if (adapter.itemCount > 0) {
                                binding.rvChatMessages.smoothScrollToPosition(adapter.itemCount - 1)
                            }
                        }
                    }
                    is ChatDetailUiState.FriendRequestSuccess -> {
                        Toast.makeText(requireContext(), "친구 신청을 보냈습니다.", Toast.LENGTH_SHORT).show()
                        binding.btnRequestFriend.isEnabled = false
                        binding.btnRequestFriend.text = "신청완료"
                    }
                    is ChatDetailUiState.Error -> {
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                    }
                    else -> Unit
                }
            }
        }
    }
private fun updateLockState(status: String) {
    // LOCKED 상태일 때만 입력창 비활성화 및 안내 노출
    if (status == "LOCKED") {
        binding.layoutLockNotice.visibility = View.VISIBLE
        binding.ivLockIcon.visibility = View.VISIBLE
        binding.etMessage.isEnabled = false
        binding.btnSend.isEnabled = false
        binding.etMessage.hint = "대화가 잠겨있습니다"
    } else {
        binding.layoutLockNotice.visibility = View.GONE
        binding.ivLockIcon.visibility = View.GONE
        binding.etMessage.isEnabled = true
        binding.btnSend.isEnabled = true
        binding.etMessage.hint = "메시지를 입력하세요"
    }
}

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
