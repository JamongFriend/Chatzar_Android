package com.example.chatzar_android.feature.match

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.chatzar_android.R
import com.example.chatzar_android.core.network.ApiClient
import com.example.chatzar_android.data.remote.api.MatchApi
import com.example.chatzar_android.data.remote.dto.MatchConditionRequest
import com.example.chatzar_android.data.repository.MatchRepository
import com.example.chatzar_android.databinding.MatchFragmentStartBinding
import com.example.chatzar_android.feature.match.preference.MatchPreferenceViewModel
import com.example.chatzar_android.feature.match.preference.MatchPreferenceViewModelFactory
import com.example.chatzar_android.feature.match.preference.PreferenceUiState
import kotlinx.coroutines.launch

class MatchStartFragment : Fragment() {
    private var _binding: MatchFragmentStartBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: MatchPreferenceViewModel

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

        setupViewModel()
        setupListeners()
        observeState()

        viewModel.fetchPreference()
    }

    private fun setupViewModel() {
        val api = ApiClient.retrofit.create(MatchApi::class.java)
        val repository = MatchRepository(api)
        val factory = MatchPreferenceViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[MatchPreferenceViewModel::class.java]
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

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect { state ->
                when (state) {
                    is PreferenceUiState.Success -> {
                        state.preference?.let { updateUi(it) }
                    }
                    is PreferenceUiState.Error -> {
                        binding.tvPrefSummary.text = "조건을 불러오는 데 실패했습니다."
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun updateUi(preference: MatchConditionRequest) {
        val gender = when (preference.genderPreference) {
            "MALE" -> "남성"
            "FEMALE" -> "여성"
            else -> "성별무관"
        }
        val age = if (preference.minAge != null && preference.maxAge != null) {
            "${preference.minAge}~${preference.maxAge}세"
        } else {
            "나이무관"
        }
        val topic = if (!preference.topic.isNullOrBlank()) "주제: ${preference.topic}" else null
        val region = if (!preference.region.isNullOrBlank()) "지역: ${preference.region}" else null

        val summary = listOfNotNull(gender, age, topic, region).joinToString("\n")
        binding.tvPrefSummary.text = summary
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
