package com.konkuk.kureal.posting

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.konkuk.kureal.R
import com.konkuk.kureal.home.HomeActivity
import com.konkuk.kureal.posting.fragments.one.OnePostFragment
import com.konkuk.kureal.util.onBackPressedListener


class PostActivity : AppCompatActivity(){
    private lateinit var navController: NavController
    //private val fragmentOnboardFour by lazy { FourOnboardFragment() }
    private val fragmentPostOne by lazy { OnePostFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)
        initNavController()
        changeFragment(fragmentPostOne)
    }

    private fun initNavController() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.host_nav_fragment_onboard) as NavHostFragment
        navController = navHostFragment.navController
    }

    private fun changeFragment(fragment: Fragment) {
        Log.d("fragmentChanged", fragment.toString())
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.host_nav_fragment_onboard, fragment)
            .commit()
    }

    override fun onBackPressed() {

        //프래그먼트 onBackPressedListener사용
        val fragmentList = supportFragmentManager.fragments
        for (fragment in fragmentList) {
            if (fragment is onBackPressedListener) {
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }
        }
    }

}