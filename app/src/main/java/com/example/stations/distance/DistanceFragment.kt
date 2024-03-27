package com.example.stations.distance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.stations.databinding.FragmentDistanceBinding
import com.example.stations.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class DistanceFragment : BaseFragment() {

    override val viewModel: DistanceViewModel by viewModel()
    private lateinit var binding: FragmentDistanceBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        setupBinding(inflater)

        return binding.root
    }

    private fun setupBinding(inflater: LayoutInflater) {
        binding = FragmentDistanceBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
    }
}