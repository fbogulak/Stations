package com.example.stations.distance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import com.example.stations.R
import com.example.stations.databinding.FragmentDistanceBinding
import com.example.stations.ui.base.BaseFragment
import com.example.stations.utils.LoadingStatus
import com.example.stations.utils.toLocalDateTime
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.time.Duration
import java.time.LocalDateTime

class DistanceFragment : BaseFragment() {

    override val viewModel: DistanceViewModel by viewModel()
    private lateinit var binding: FragmentDistanceBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentDistanceBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupMenu()

        setupStationsData()
    }

    private fun setupObservers() {
        viewModel.status.observe(viewLifecycleOwner) {
            when (it) {
                LoadingStatus.NO_INTERNET -> {
                    binding.progressBar.visibility = View.GONE
                    showSnackBar(R.string.error_offline)
                }

                LoadingStatus.LOADING -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                LoadingStatus.ERROR -> {
                    binding.progressBar.visibility = View.GONE
                    handleError()
                }

                LoadingStatus.SUCCESS -> {
                    binding.progressBar.visibility = View.GONE
                }

                else -> {
                    binding.progressBar.visibility = View.GONE
                    showToast(R.string.uncommon_error)
                }
            }
        }
    }

    private fun setupMenu() {
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_distance, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.action_refresh -> {
                        if (hasNetworkConnection()) {
                            viewModel.refreshData()
                        } else {
                            viewModel.setHasNoInternet()
                        }
                        return true
                    }
                }
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun setupStationsData() {
        val lastRefreshTime = viewModel.lastRefreshTime?.toLocalDateTime()
        if (lastRefreshTime == null) {
            showSnackBar(R.string.connection_required)
        } else if (
            Duration.between(lastRefreshTime, LocalDateTime.now()).toHours() >= 24
        ) {
            if (hasNetworkConnection()) {
                viewModel.refreshData()
            } else {
                showSnackBar(R.string.time_to_refresh_please_connect)
            }
        }
    }
}