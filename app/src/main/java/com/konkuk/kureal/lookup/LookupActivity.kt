package com.konkuk.kureal.lookup

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.konkuk.kureal.R
import com.konkuk.kureal.databinding.ActivityLookupBinding
import com.konkuk.kureal.lookup.fragments.one.OneLookupFragment

class LookupActivity : AppCompatActivity(){
    private var _binding: ActivityLookupBinding? = null
    private val binding get() = _binding ?: error("View를 참조하기 위해 binding이 초기화되지 않았습니다.")
//    private val viewModel: HomeViewModel by viewModels() //위임초기화

    private val fragmentLookupOne by lazy { OneLookupFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLookupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initNavController()
    }

    private fun initNavController() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_lookup) as NavHostFragment
        changeFragment(fragmentLookupOne)
    }

    private fun changeFragment(fragment: Fragment) {
        Log.d("fragmentChanged", fragment.toString())
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.nav_host_lookup, fragment)
            .commit()
    }
}