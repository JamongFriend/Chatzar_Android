package com.example.chatzar_android.feature.auth

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.chatzar_android.R
import com.example.chatzar_android.core.network.ApiClient
import com.example.chatzar_android.data.remote.api.AuthApi
import com.example.chatzar_android.data.repository.AuthRepository
import kotlinx.coroutines.launch

class LoginActivity: ComponentActivity() {
    private lateinit var vm: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val authApi = ApiClient.retrofit.create(AuthApi::class.java)
        val repo = AuthRepository(authApi)
        vm = ViewModelProvider(this, LoginViewModelFactory(repo))[LoginViewModel::class.java]

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        btnLogin.setOnClickListener {
            vm.login(etEmail.text.toString(), etPassword.text.toString())
        }

        lifecycleScope.launch {
            vm.state.collect { state ->
                when (state) {
                    is LoginUiState.Idle -> Unit
                    is LoginUiState.Loading -> Toast.makeText(this@LoginActivity, "로그인 중...", Toast.LENGTH_SHORT).show()
                    is LoginUiState.Success -> {
                        Toast.makeText(this@LoginActivity, "성공! memberId=${state.data.memberId}", Toast.LENGTH_LONG).show()
                        finish()
                    }
                    is LoginUiState.Error -> Toast.makeText(this@LoginActivity, state.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
