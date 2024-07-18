package com.adi121.statussaverupdate.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.adi121.statussaverupdate.databinding.FragmentMoreFragementBinding

class MoreFragment : Fragment() {

    private lateinit var binding: FragmentMoreFragementBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMoreFragementBinding.inflate(inflater)
        return binding.root
    }


}