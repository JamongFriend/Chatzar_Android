package com.example.chatzar_android.feature.friend

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.chatzar_android.data.remote.dto.FriendshipResponse
import com.example.chatzar_android.databinding.FriendItemRequestBinding

class FriendRequestsAdapter(
    private val onAcceptClick: (FriendshipResponse) -> Unit,
    private val onRejectClick: (FriendshipResponse) -> Unit
) : ListAdapter<FriendshipResponse, FriendRequestsAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = FriendItemRequestBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: FriendItemRequestBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(request: FriendshipResponse) {
            binding.tvRequesterName.text = request.requesterName
            binding.tvRequestTime.text = "대기 중"
            
            binding.btnAccept.setOnClickListener { onAcceptClick(request) }
            binding.btnReject.setOnClickListener { onRejectClick(request) }
        }
    }

    object DiffCallback : DiffUtil.ItemCallback<FriendshipResponse>() {
        override fun areItemsTheSame(oldItem: FriendshipResponse, newItem: FriendshipResponse): Boolean {
            return oldItem.friendshipId == newItem.friendshipId
        }

        override fun areContentsTheSame(oldItem: FriendshipResponse, newItem: FriendshipResponse): Boolean {
            return oldItem == newItem
        }
    }
}
