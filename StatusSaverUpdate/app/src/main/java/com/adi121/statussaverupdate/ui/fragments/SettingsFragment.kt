package com.adi121.statussaverupdate.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import com.adi121.statussaverupdate.R
import com.adi121.statussaverupdate.databinding.FragmentSettingsBinding
import com.adi121.statussaverupdate.utils.SharedPrefs
import com.adi121.statussaverupdate.utils.showCustomToast


class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding

    private val sharedPrefs by lazy {
        SharedPrefs.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSettingsBinding.inflate(inflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (sharedPrefs?.isWhatsAppMode!!) {
            binding.tvChangeMode.text = "Change to WhatsApp Business"
        } else {
            binding.tvChangeMode.text = "Change to WhatsApp"
        }

        binding.tvChangeMode.setOnClickListener {
            val currentModeValue = sharedPrefs?.isWhatsAppMode
            if (currentModeValue == true) {
                binding.tvChangeMode.text = "Change to WhatsApp"
                requireContext().showCustomToast("Changed to WhatsApp Business")

            } else {
                binding.tvChangeMode.text = "Change to WhatsApp Business"
                requireContext().showCustomToast("Changed to WhatsApp")
            }
            sharedPrefs?.isWhatsAppMode = !currentModeValue!!
        }

        if (sharedPrefs?.isDarkTheme!!) {
            binding.tvSwitchThemeMode.text = "Switch to Light Mode"
        } else {
            binding.tvSwitchThemeMode.text = "Switch to Dark Mode"
        }



        binding.tvSwitchThemeMode.setOnClickListener {
            if (sharedPrefs?.isDarkTheme!!) {
                AppCompatDelegate
                    .setDefaultNightMode(
                        AppCompatDelegate
                            .MODE_NIGHT_NO
                    )
                sharedPrefs?.isDarkTheme = false
                binding.tvSwitchThemeMode.text = "Switch to Dark Mode"

            } else {
                AppCompatDelegate
                    .setDefaultNightMode(
                        AppCompatDelegate
                            .MODE_NIGHT_YES
                    );
                sharedPrefs?.isDarkTheme = true
                binding.tvSwitchThemeMode.text = "Switch to Light Mode"
            }
        }

        binding.tvRateApp.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("market://details?id=com.vickyneji.statussaver")
            startActivity(intent)
        }

        binding.tvShareAPp.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.putExtra(Intent.EXTRA_TEXT, resources.getString(R.string.appLink))
            intent.type = "text/plain"
            startActivity(Intent.createChooser(intent, "Share To:"))
        }
    }


}