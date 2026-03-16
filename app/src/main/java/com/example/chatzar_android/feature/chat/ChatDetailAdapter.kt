package com.example.chatzar_android.feature.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatzar_android.data.remote.dto.MessageResponse
import com.example.chatzar_android.databinding.ChatItemLeftBinding
import com.example.chatzar_android.databinding.ChatItemRightBinding

class ChatDetailAdapter(private val myMemberId: Long) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val messages = mutableListOf<MessageResponse>()

    companion object {
        private const val TYPE_LEFT = 0
        private const val TYPE_RIGHT = 1
    }

    fun submitList(newList: List<MessageResponse>) {
        messages.clear()
        messages.addAll(newList)
        notifyDataSetChanged()
    }

    fun addMessage(message: MessageResponse) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].senderId == myMemberId) TYPE_RIGHT else TYPE_LEFT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_RIGHT) {
            val binding = ChatItemRightBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            RightViewHolder(binding)
        } else {
            val binding = ChatItemLeftBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            LeftViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        if (holder is RightViewHolder) holder.bind(message)
        else if (holder is LeftViewHolder) holder.bind(message)
    }

    override fun getItemCount(): Int = messages.size

    inner class LeftViewHolder(private val binding: ChatItemLeftBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: MessageResponse) {
            binding.tvUserName.text = message.senderNickname
            binding.tvMessage.text = message.content
            binding.tvTime.text = message.createdAt // 필요시 포맷팅
        }
    }

    inner class RightViewHolder(private val binding: ChatItemRightBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: MessageResponse) {
            binding.tvMessage.text = message.content
            binding.tvTime.text = message.createdAt
        }
    }
}
