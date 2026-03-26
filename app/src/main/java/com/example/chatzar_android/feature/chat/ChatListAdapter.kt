package com.example.chatzar_android.feature.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.chatzar_android.data.remote.dto.ChatRoomResponse
import com.example.chatzar_android.databinding.ChatItemRoomBinding

class ChatListAdapter(
    private val onRoomClick: (ChatRoomResponse) -> Unit
) : ListAdapter<ChatRoomResponse, ChatListAdapter.ChatRoomViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatRoomViewHolder {
        val binding = ChatItemRoomBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ChatRoomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatRoomViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ChatRoomViewHolder(private val binding: ChatItemRoomBinding) :
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(room: ChatRoomResponse) {
            binding.tvRoomName.text = room.otherNickname
            binding.tvLastMessage.text = "대화방 입장하기"
            binding.tvTime.text = "" // 추후 메시지 시간 추가 가능

            binding.root.setOnClickListener {
                onRoomClick(room)
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<ChatRoomResponse>() {
        override fun areItemsTheSame(oldItem: ChatRoomResponse, newItem: ChatRoomResponse): Boolean {
            return oldItem.roomId == newItem.roomId
        }

        override fun areContentsTheSame(oldItem: ChatRoomResponse, newItem: ChatRoomResponse): Boolean {
            return oldItem == newItem
        }
    }
}
