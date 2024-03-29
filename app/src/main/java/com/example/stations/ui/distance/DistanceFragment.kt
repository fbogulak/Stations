package com.example.stations.ui.distance

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.stations.MainActivity
import com.example.stations.R
import com.example.stations.databinding.FragmentDistanceBinding
import com.example.stations.ui.base.BaseFragment
import com.example.stations.ui.distance.adapters.StationsListAdapter
import com.example.stations.utils.LoadingStatus
import com.example.stations.utils.toLocalDateTime
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.search.SearchView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.time.Duration
import java.time.LocalDateTime
import java.util.Timer
import kotlin.concurrent.schedule

class DistanceFragment : BaseFragment() {

    override val viewModel: DistanceViewModel by viewModel()
    private lateinit var binding: FragmentDistanceBinding
    private val backCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            binding.apply {
                if (fromSearchView.isShowing) {
                    fromSearchView.close()
                }
                if (toSearchView.isShowing) {
                    toSearchView.close()
                }
            }
        }
    }
    private var timer = Timer()
    private lateinit var snackbar: Snackbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this, backCallback)
    }

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
        setupRecyclers()
        setupObservers()
        setupListeners()
        setupMenu()
        setupSnackBar()
        setupEditTexts()

        setupStationsData()
    }

    private fun setupRecyclers() {
        binding.fromRecycler.apply {
            adapter = StationsListAdapter(
                StationsListAdapter.StationListener {
                    viewModel.fromStation.value = it
                    binding.fromSearchView.close()
                }
            )
        }
        binding.toRecycler.apply {
            adapter = StationsListAdapter(
                StationsListAdapter.StationListener {
                    viewModel.toStation.value = it
                    binding.toSearchView.close()
                }
            )
        }
    }

    private fun setupObservers() {
        viewModel.status.observe(viewLifecycleOwner) {
            when (it) {
                LoadingStatus.NO_INTERNET -> {
                    binding.progressBar.hide()
                    snackbar.dismiss()
                    showSnackBar(R.string.error_offline)
                }

                LoadingStatus.LOADING -> {
                    binding.progressBar.show()
                    snackbar.show()
                }

                LoadingStatus.ERROR -> {
                    binding.progressBar.hide()
                    snackbar.dismiss()
                    handleError()
                }

                LoadingStatus.SUCCESS -> {
                    binding.progressBar.hide()
                    snackbar.dismiss()
                }

                else -> {
                    binding.progressBar.hide()
                    snackbar.dismiss()
                    showToast(R.string.uncommon_error)
                }
            }
        }
    }

    private fun setupListeners() {
        binding.fromSearchView.addTransitionListener { _, _, newState ->
            if (newState == SearchView.TransitionState.SHOWING) {
                backCallback.isEnabled = true
                (activity as? MainActivity)?.setToolbarTitle(R.string.from_station_hint)
            } else if (newState == SearchView.TransitionState.HIDING) {
                backCallback.isEnabled = false
                (activity as? MainActivity)?.setToolbarTitle(R.string.app_name)
            }
        }
        binding.toSearchView.addTransitionListener { _, _, newState ->
            if (newState == SearchView.TransitionState.SHOWING) {
                backCallback.isEnabled = true
                (activity as? MainActivity)?.setToolbarTitle(R.string.to_station_hint)
            } else if (newState == SearchView.TransitionState.HIDING) {
                backCallback.isEnabled = false
                (activity as? MainActivity)?.setToolbarTitle(R.string.app_name)
            }
        }
        binding.fromSearchView.editText.doAfterTextChanged {
            searchStations(binding.fromRecycler, it)
        }
        binding.toSearchView.editText.doAfterTextChanged {
            searchStations(binding.toRecycler, it)
        }
        binding.fromSearchBar.textView.doOnTextChanged { _, _, _, _ ->
            binding.fromErrorText.visibility = View.GONE
        }
        binding.toSearchBar.textView.doOnTextChanged { _, _, _, _ ->
            binding.toErrorText.visibility = View.GONE
        }
        binding.calculateButton.setOnClickListener {
            if (validateStations()) {
                viewModel.calculateDistance()
                binding.distanceGroup.visibility = View.VISIBLE
            }
        }
    }

    private fun searchStations(recycler: RecyclerView, s: Editable?) {
        timer.cancel()
        val sleep = when (s?.length) {
            0 -> 0L
            1 -> 1000L
            2, 3 -> 700L
            4, 5 -> 500L
            else -> 300L
        }
        timer = Timer()
        timer.schedule(sleep) {
            lifecycleScope.launch(Dispatchers.IO) {
                viewModel.getStations(s.toString())
                withContext(Dispatchers.Main) {
                    (recycler.adapter as StationsListAdapter).submitList(viewModel.stations) {
                        recycler.scrollToPosition(0)
                    }
                }
            }
        }
    }

    private fun validateStations(): Boolean {
        var validationPassed = true
        if (viewModel.fromStation.value == null) {
            binding.fromErrorText.visibility = View.VISIBLE
            validationPassed = false
        } else {
            binding.fromErrorText.visibility = View.GONE
        }
        if (viewModel.toStation.value == null) {
            binding.toErrorText.visibility = View.VISIBLE
            validationPassed = false
        } else {
            binding.toErrorText.visibility = View.GONE
        }
        if (viewModel.toStation.value == null || viewModel.fromStation.value == null)
            return validationPassed
        if (viewModel.fromStation.value?.latitude == null || viewModel.fromStation.value?.longitude == null ||
            viewModel.fromStation.value?.latitude == 0.0 || viewModel.fromStation.value?.longitude == 0.0
        ) {
            MaterialAlertDialogBuilder(requireContext())
                .setMessage(R.string.from_station_error)
                .setPositiveButton(R.string.ok) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
            validationPassed = false
        }
        if (viewModel.toStation.value?.latitude == null || viewModel.toStation.value?.longitude == null ||
            viewModel.toStation.value?.latitude == 0.0 || viewModel.toStation.value?.longitude == 0.0
        ) {
            MaterialAlertDialogBuilder(requireContext())
                .setMessage(R.string.to_station_error)
                .setPositiveButton(R.string.ok) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
            validationPassed = false
        }
        return validationPassed
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

                    R.id.action_info -> {
                        MaterialAlertDialogBuilder(requireContext())
                            .setTitle(R.string.how_it_works)
                            .setIcon(R.drawable.ic_info)
                            .setMessage(R.string.info_msg)
                            .setPositiveButton(R.string.ok) { dialog, _ ->
                                dialog.dismiss()
                            }
                            .show()
                        return true
                    }
                }
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun setupSnackBar() {
        snackbar = Snackbar.make(
            this.requireView(),
            getString(R.string.stations_loading),
            Snackbar.LENGTH_INDEFINITE
        )
    }

    private fun setupEditTexts() {
        binding.fromSearchView.editText.setSelectAllOnFocus(true)
        binding.toSearchView.editText.setSelectAllOnFocus(true)
    }

    private fun setupStationsData() {
        val lastRefreshTime = viewModel.lastRefreshTime?.toLocalDateTime()
        if (lastRefreshTime == null) {
            if (hasNetworkConnection()) {
                viewModel.refreshData()
            } else {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.no_internet_title)
                    .setIcon(R.drawable.ic_cloud_off)
                    .setMessage(R.string.no_internet_msg)
                    .setPositiveButton(R.string.ok) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
        } else if (
            Duration.between(lastRefreshTime, LocalDateTime.now()).toHours() >= 24
        ) {
            if (hasNetworkConnection()) {
                viewModel.refreshData()
            } else {
                showSnackBar(R.string.time_to_refresh_please_connect)
            }
        }
        if (viewModel.distance.value != null) binding.distanceGroup.visibility = View.VISIBLE
    }

    private fun SearchView.close() {
        hide()
        backCallback.isEnabled = false
        (activity as? MainActivity)?.setToolbarTitle(R.string.app_name)
    }
}