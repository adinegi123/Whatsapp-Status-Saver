package com.adi121.statussaverupdate.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.adi121.statussaverupdate.databinding.ActivityMainBinding
import com.adi121.statussaverupdate.utils.SharedPrefs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LauncherActivity : AppCompatActivity() {

    lateinit var binding:ActivityMainBinding

    private val sharedPrefs by lazy {
        SharedPrefs.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if(sharedPrefs?.isDarkTheme!!){
            AppCompatDelegate
                .setDefaultNightMode(
                    AppCompatDelegate
                        .MODE_NIGHT_YES);

        }else{
            AppCompatDelegate
                .setDefaultNightMode(
                    AppCompatDelegate
                        .MODE_NIGHT_NO);
        }



        CoroutineScope(Dispatchers.Main).launch {
            delay(1500)
            startActivity(Intent(this@LauncherActivity,DashboardActivity::class.java))
            finish()
        }
    }
}