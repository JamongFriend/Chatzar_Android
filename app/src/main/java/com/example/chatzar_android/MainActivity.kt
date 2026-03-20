package com.example.chatzar_android

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.chatzar_android.core.network.ApiClient
import com.example.chatzar_android.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // ApiClient 초기화
        ApiClient.init(applicationContext)

        this.enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v: View, insets: WindowInsetsCompat ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        setupNavigation()
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.main_container) as NavHostFragment
        val navController = navHostFragment.navController

        // BottomNavigationView와 NavController 연결
        binding.bottomNav.setupWithNavController(navController)

        // 특정 화면에서만 하단 바 보이기/숨기기
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.loginFragment, R.id.signupFragment -> {
                    binding.bottomNav.visibility = View.GONE
                }
                R.id.chatDetailFragment, R.id.matchProcessFragment -> {
                    binding.bottomNav.visibility = View.GONE
                }
                else -> {
                    binding.bottomNav.visibility = View.VISIBLE
                }
            }
        }
    }
}
