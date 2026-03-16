package com.example.chatzar_android.feature.match.preference

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.chatzar_android.core.network.ApiClient
import com.example.chatzar_android.data.remote.api.MatchApi
import com.example.chatzar_android.data.repository.MatchRepository
import com.example.chatzar_android.databinding.MatchFragmentPreferenceBinding
import com.example.chatzar_android.R
import kotlinx.coroutines.launch

class MatchPreferenceFragment : Fragment() {
    private var _binding: MatchFragmentPreferenceBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: MatchPreferenceViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MatchFragmentPreferenceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupListeners()
        observeState()
    }

    private fun setupViewModel() {
        val api = ApiClient.retrofit.create(MatchApi::class.java)
        val repository = MatchRepository(api)
        val factory = MatchPreferenceViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[MatchPreferenceViewModel::class.java]
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnSavePreference.setOnClickListener {
            savePreference()
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect { state ->
                when (state) {
                    is PreferenceUiState.Idle -> Unit
                    is PreferenceUiState.Loading -> {
                        binding.btnSavePreference.isEnabled = false
                        Toast.makeText(requireContext(), "저장 중...", Toast.LENGTH_SHORT).show()
                    }
                    is PreferenceUiState.Success -> {
                        binding.btnSavePreference.isEnabled = true
                        Toast.makeText(requireContext(), "저장되었습니다.", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_matchPreferenceFragment_to_matchStartFragment)
                    }
                    is PreferenceUiState.Error -> {
                        binding.btnSavePreference.isEnabled = true
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun savePreference() {
        val gender = when (binding.rgGender.checkedRadioButtonId) {
            binding.rbGenderMale.id -> "MALE"
            binding.rbGenderFemale.id -> "FEMALE"
            else -> "ANY"
        }
        val minAge = binding.etMinAge.text.toString().toIntOrNull() ?: 20
        val maxAge = binding.etMaxAge.text.toString().toIntOrNull() ?: 40
        val topic = binding.etTopic.text.toString()
        val region = binding.etRegion.text.toString()

        viewModel.updatePreference(gender, minAge, maxAge, topic, region)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
