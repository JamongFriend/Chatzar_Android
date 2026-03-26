package com.example.chatzar_android.feature.match.preference

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.chatzar_android.data.remote.dto.MatchConditionRequest
import com.example.chatzar_android.data.repository.MatchRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class PreferenceUiState {
    object Idle : PreferenceUiState()
    object Loading : PreferenceUiState()
    data class Success(val preference: MatchConditionRequest? = null) : PreferenceUiState()
    data class Error(val message: String) : PreferenceUiState()
}

class MatchPreferenceViewModel(private val repository: MatchRepository) : ViewModel() {

    private val _state = MutableStateFlow<PreferenceUiState>(PreferenceUiState.Idle)
    val state: StateFlow<PreferenceUiState> = _state

    fun fetchPreference() {
        viewModelScope.launch {
            _state.value = PreferenceUiState.Loading
            try {
                val response = repository.getPreference()
                if (response.isSuccessful) {
                    _state.value = PreferenceUiState.Success(response.body())
                } else {
                    _state.value = PreferenceUiState.Error("데이터를 불러오는 데 실패했습니다: ${response.code()}")
                }
            } catch (e: Exception) {
                _state.value = PreferenceUiState.Error(e.message ?: "알 수 없는 오류가 발생했습니다.")
            }
        }
    }

    fun updatePreference(gender: String, minAge: Int, maxAge: Int, topic: String, region: String) {
        viewModelScope.launch {
            _state.value = PreferenceUiState.Loading
            try {
                val request = MatchConditionRequest(gender, minAge, maxAge, topic, region)
                val response = repository.updatePreference(request)
                if (response.isSuccessful) {
                    _state.value = PreferenceUiState.Success()
                } else {
                    _state.value = PreferenceUiState.Error("저장에 실패했습니다: ${response.code()}")
                }
            } catch (e: Exception) {
                _state.value = PreferenceUiState.Error(e.message ?: "알 수 없는 오류가 발생했습니다.")
            }
        }
    }
}

class MatchPreferenceViewModelFactory(private val repository: MatchRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MatchPreferenceViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MatchPreferenceViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
