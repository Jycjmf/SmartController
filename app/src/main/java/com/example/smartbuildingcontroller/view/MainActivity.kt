package com.example.smartbuildingcontroller.view

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.smartbuildingcontroller.FragmentDetailDirections
import com.example.smartbuildingcontroller.R
import com.example.smartbuildingcontroller.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        val window: Window = window
        val decorView: View = window.getDecorView()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val wic = decorView.windowInsetsController
            wic!!.setSystemBarsAppearance(
                APPEARANCE_LIGHT_STATUS_BARS,
                APPEARANCE_LIGHT_STATUS_BARS
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        binding=DataBindingUtil.setContentView(this,R.layout.activity_main)
        val navHostFragment=supportFragmentManager.findFragmentById(R.id.fragment2) as NavHostFragment
        binding.topItem.setNavigationChangeListener { view, position ->
            if (position == 1) {
                navHostFragment.findNavController()
                    .navigate(StatusFragmentDirections.actionStatusFragmentToFragmentDetail())
            }
            else
                findNavController(R.id.fragment2).navigate(FragmentDetailDirections.actionFragmentDetailToStatusFragment())
        }
    }
    fun setBottomNavigationVisibility(visibility: Int) {
        // get the reference of the bottomNavigationView and set the visibility.
        binding.bottomNavigationView.visibility = visibility
    }
    fun setTopNavigationVisibility(visibility: Int) {
        // get the reference of the bottomNavigationView and set the visibility.
        binding.topItem.visibility = visibility
    }

}