package com.example.chatzar_android.feature.friend

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.chatzar_android.databinding.FriendFragmentListBinding

class FriendListFragment : Fragment() {
    private var _binding: FriendFragmentListBinding? = null
    private val binding get() = _binding!!

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
        
        // 여기에 친구 목록 로딩 로직 추가 가능
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
