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
import com.example.chatzar_android.data.remote.api.MessageApi
import com.example.chatzar_android.data.repository.MessageRepository
import com.example.chatzar_android.databinding.ChatFragmentDetailBinding
import kotlinx.coroutines.launch

class ChatDetailFragment : Fragment() {
    private var _binding: ChatFragmentDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ChatDetailViewModel
    private lateinit var adapter: ChatDetailAdapter
    private var roomId: Long = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ChatFragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        roomId = arguments?.getString("roomId")?.toLongOrNull() ?: -1
        if (roomId == -1L) {
            Toast.makeText(requireContext(), "잘못된 접근입니다.", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
            return
        }

        setupViewModel()
        setupRecyclerView()
        setupListeners()
        observeState()

        // 초기 데이터 로딩 및 WebSocket 연결
        viewModel.getMessages(roomId)
        viewModel.connectAndSubscribe(roomId, "ws://10.0.2.2:8080/ws/chat") // 에뮬레이터 기준 로컬 서버 주소
    }

    private fun setupViewModel() {
        val api = ApiClient.retrofit.create(MessageApi::class.java)
        val repository = MessageRepository(api)
        val factory = ChatDetailViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[ChatDetailViewModel::class.java]
    }

    private fun setupRecyclerView() {
        // TODO: 실제 로그인한 유저의 ID를 넘겨줘야 함 (지금은 임시 0)
        adapter = ChatDetailAdapter(myMemberId = 0L) 
        binding.rvChatMessages.apply {
            layoutManager = LinearLayoutManager(requireContext()).apply {
                stackFromEnd = true // 메시지가 아래서부터 쌓임
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

        // 임시로 잠금 해제 (테스트용)
        binding.layoutLockNotice.visibility = View.GONE
        binding.etMessage.isEnabled = true
        binding.btnSend.isEnabled = true
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect { state ->
                when (state) {
                    is ChatDetailUiState.HistorySuccess -> {
                        adapter.submitList(state.messages)
                        binding.rvChatMessages.scrollToPosition(adapter.itemCount - 1)
                    }
                    is ChatDetailUiState.NewMessage -> {
                        adapter.addMessage(state.message)
                        binding.rvChatMessages.smoothScrollToPosition(adapter.itemCount - 1)
                    }
                    is ChatDetailUiState.Error -> {
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
