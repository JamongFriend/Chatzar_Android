package com.example.chatzar_android.feature.match

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.chatzar_android.R
import com.example.chatzar_android.databinding.MatchFragmentStartBinding

class MatchStartFragment : Fragment() {
    private var _binding: MatchFragmentStartBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MatchFragmentStartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
    }

    private fun setupListeners() {
        // 매칭 시작 버튼
        binding.btnStartMatching.setOnClickListener {
            findNavController().navigate(R.id.action_matchStartFragment_to_matchProcessFragment)
        }

        // 조건 수정 버튼 (ID: btn_edit_preference -> btnEditPreference)
        binding.btnEditPreference.setOnClickListener {
            findNavController().navigate(R.id.action_matchStartFragment_to_matchPreferenceFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
