package com.example.chatzar_android.feature.friend

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.chatzar_android.data.remote.dto.FriendListResponse
import com.example.chatzar_android.data.remote.dto.FriendshipResponse
import com.example.chatzar_android.databinding.ItemFriendBinding
import com.example.chatzar_android.databinding.ItemFriendRequestBinding

sealed class FriendListItem {
    data class Request(val request: FriendshipResponse) : FriendListItem()
    data class Friend(val friend: FriendListResponse) : FriendListItem()
    data class Header(val title: String) : FriendListItem()
}

class FriendListAdapter(
    private val onAcceptClick: (Long) -> Unit,
    private val onFriendClick: (FriendListResponse) -> Unit
) : ListAdapter<FriendListItem, RecyclerView.ViewHolder>(DiffCallback) {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_REQUEST = 1
        private const val TYPE_FRIEND = 2
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is FriendListItem.Header -> TYPE_HEADER
            is FriendListItem.Request -> TYPE_REQUEST
            is FriendListItem.Friend -> TYPE_FRIEND
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_REQUEST -> {
                val binding = ItemFriendRequestBinding.inflate(inflater, parent, false)
                RequestViewHolder(binding)
            }
            TYPE_FRIEND -> {
                val binding = ItemFriendBinding.inflate(inflater, parent, false)
                FriendViewHolder(binding)
            }
            else -> {
                val view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false)
                HeaderViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when {
            holder is RequestViewHolder && item is FriendListItem.Request -> holder.bind(item.request)
            holder is FriendViewHolder && item is FriendListItem.Friend -> holder.bind(item.friend)
            holder is HeaderViewHolder && item is FriendListItem.Header -> holder.bind(item.title)
        }
    }

    inner class RequestViewHolder(private val binding: ItemFriendRequestBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(request: FriendshipResponse) {
            binding.tvRequesterNickname.text = request.requesterName
            binding.tvRequesterInfo.text = "${request.requesterAge}세 | 친구 신청"
            binding.btnAcceptFriend.setOnClickListener {
                onAcceptClick(request.friendshipId)
            }
        }
    }

    inner class FriendViewHolder(private val binding: ItemFriendBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(friend: FriendListResponse) {
            binding.tvFriendNickname.text = friend.nickname
            binding.tvFriendTag.text = "#${friend.tag}"
            binding.tvFriendInfo.text = "${friend.age}세"
            binding.root.setOnClickListener { onFriendClick(friend) }
        }
    }

    inner class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val textView: android.widget.TextView = view.findViewById(android.R.id.text1)
        fun bind(title: String) {
            textView.text = title
            textView.textSize = 14f
            textView.setTextColor(android.graphics.Color.GRAY)
            textView.setPadding(48, 24, 48, 24)
        }
    }

    object DiffCallback : DiffUtil.ItemCallback<FriendListItem>() {
        override fun areItemsTheSame(oldItem: FriendListItem, newItem: FriendListItem): Boolean {
            return when {
                oldItem is FriendListItem.Request && newItem is FriendListItem.Request ->
                    oldItem.request.friendshipId == newItem.request.friendshipId
                oldItem is FriendListItem.Friend && newItem is FriendListItem.Friend ->
                    oldItem.friend.friendshipId == newItem.friend.friendshipId
                oldItem is FriendListItem.Header && newItem is FriendListItem.Header ->
                    oldItem.title == newItem.title
                else -> false
            }
        }

        override fun areContentsTheSame(oldItem: FriendListItem, newItem: FriendListItem): Boolean {
            return oldItem == newItem
        }
    }
}
