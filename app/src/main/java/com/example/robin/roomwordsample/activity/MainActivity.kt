package com.example.robin.roomwordsample.activity

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import com.example.robin.roomwordsample.R
import com.example.robin.roomwordsample.databinding.ActivityMainBinding
import com.example.robin.roomwordsample.utils.AppConstants
import com.example.robin.roomwordsample.utils.StoreSession
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        if (StoreSession.read(AppConstants.NIGHT_MODE)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                } else {
                    @Suppress("DEPRECATION")
                    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                }
            }
        }

//        val navController = this.findNavController(R.id.myNavHostFragment)
//        NavigationUI.setupActionBarWithNavController(this, navController)
//
//        supportActionBar?.setDisplayShowTitleEnabled(false)
//        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
//        supportActionBar?.setCustomView(R.layout.custom_toolbar)
//        supportActionBar?.elevation = 0F
//
//        navController.addOnDestinationChangedListener { controller, destination, arguments ->
//            supportActionBar?.customView?.title?.text = destination.label.toString()
//            Log.e("TAG", destination.label.toString() + " title")
//        }

    }

//    override fun onSupportNavigateUp(): Boolean {
//        val navController = this.findNavController(R.id.myNavHostFragment)
//        return navController.navigateUp() || super.onSupportNavigateUp()
//    }

}
