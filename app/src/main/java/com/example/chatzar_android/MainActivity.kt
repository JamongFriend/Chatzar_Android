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
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0) // Bottom padding is handled by BottomNav
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

        // 클릭 리스너를 명시적으로 설정하여 동작 확인
        binding.bottomNav.setOnItemSelectedListener { item ->
            // 이미 현재 위치라면 다시 이동하지 않음
            if (navController.currentDestination?.id == item.itemId) {
                return@setOnItemSelectedListener true
            }

            when (item.itemId) {
                R.id.friendListFragment -> {
                    navController.navigate(R.id.friendListFragment)
                    true
                }
                R.id.chatListFragment -> {
                    navController.navigate(R.id.chatListFragment)
                    true
                }
                R.id.matchStartFragment -> {
                    navController.navigate(R.id.matchStartFragment)
                    true
                }
                R.id.myPageFragment -> {
                    navController.navigate(R.id.myPageFragment)
                    true
                }
                else -> false
            }
        }

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
