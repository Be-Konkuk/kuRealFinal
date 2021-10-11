package com.konkuk.kureal.onboarding

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.konkuk.kureal.R
import com.konkuk.kureal.onboarding.fragments.four.FourOnboardFragment

class OnboardActivity : AppCompatActivity(){
    private lateinit var navController: NavController
    private val fragmentOnboardFour by lazy { FourOnboardFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboard)
        initNavController()
        changeFragment(fragmentOnboardFour)
    }

    private fun initNavController() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.host_nav_fragment_onboard) as NavHostFragment
        navController = navHostFragment.navController
    }

    private fun changeFragment(fragment: Fragment) {
        Log.d("fragmentChangd", fragment.toString())
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.host_nav_fragment_onboard, fragment)
            .commit()
    }
}