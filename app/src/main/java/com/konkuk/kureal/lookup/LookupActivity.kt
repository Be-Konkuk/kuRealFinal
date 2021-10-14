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

    private var pk = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLookupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getSafeArgs()
        initNavController()
    }

    private fun initNavController() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_lookup) as NavHostFragment
        changeFragment(fragmentLookupOne)
    }

    private fun changeFragment(fragment: Fragment) {
        Log.d("fragmentChanged", fragment.toString())
        //pk 인자 넘기기 (activity -> fragment)
        val bundle = Bundle()
        bundle.putInt("pk",pk)
        fragment.arguments = bundle

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.nav_host_lookup, fragment)
            .commit()
    }

    private fun getSafeArgs(){
        if (intent.hasExtra("pk")) {
            pk = intent.getIntExtra("pk",1)
        } else {
            Log.d("LOOKUPACTIVITY","intent 에러")
        }
    }
}