package com.example.chatzar_android.feature.match

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.chatzar_android.data.repository.MatchRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class MatchUiState {
    object Idle : MatchUiState()
    object Requesting : MatchUiState()
    object Waiting : MatchUiState()
    data class Matched(val chatRoomId: Long, val partnerNickname: String) : MatchUiState()
    object Canceled : MatchUiState()
    data class Error(val message: String) : MatchUiState()
}

class MatchViewModel(private val repository: MatchRepository) : ViewModel() {

    private val _state = MutableStateFlow<MatchUiState>(MatchUiState.Idle)
    val state: StateFlow<MatchUiState> = _state

    private var pollingJob: Job? = null

    fun requestMatch() {
        viewModelScope.launch {
            _state.value = MatchUiState.Requesting
            try {
                val response = repository.requestMatch()
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result?.matched == true) {
                        _state.value = MatchUiState.Matched(result.chatRoomId!!, result.partnerNickname ?: "알 수 없음")
                    } else {
                        _state.value = MatchUiState.Waiting
                        startPollingStatus()
                    }
                } else {
                    _state.value = MatchUiState.Error("매칭 요청 실패: ${response.code()}")
                }
            } catch (e: Exception) {
                _state.value = MatchUiState.Error(e.message ?: "알 수 없는 오류")
            }
        }
    }

    private fun startPollingStatus() {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            while (true) {
                delay(3000)
                try {
                    val response = repository.getMatchStatus()
                    if (response.isSuccessful) {
                        val result = response.body()
                        if (result?.matched == true) {
                            _state.value = MatchUiState.Matched(result.chatRoomId!!, result.partnerNickname ?: "알 수 없음")
                            break
                        }
                    }
                } catch (e: Exception) { /* 에러 무시 */ }
            }
        }
    }

    fun cancelMatch() {
        pollingJob?.cancel()
        viewModelScope.launch {
            try {
                repository.cancelMatch()
                _state.value = MatchUiState.Canceled
            } catch (e: Exception) {
                _state.value = MatchUiState.Error("취소 오류")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        pollingJob?.cancel()
    }
}

class MatchViewModelFactory(private val repository: MatchRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MatchViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MatchViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
