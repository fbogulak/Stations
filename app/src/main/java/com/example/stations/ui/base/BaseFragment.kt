package com.example.stations.ui.base

import android.app.Activity
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.example.stations.R
import com.example.stations.utils.NavigationCommand
import com.google.android.material.snackbar.Snackbar

/**
 * Base Fragment to observe on the common LiveData objects
 */
abstract class BaseFragment : Fragment() {
    /**
     * Every fragment has to have an instance of a view model that extends from the BaseViewModel
     */
    abstract val viewModel: BaseViewModel
    private var isNavigating = false

    override fun onStart() {
        super.onStart()
        isNavigating = false
        viewModel.showErrorMessage.observe(this) {
            Toast.makeText(activity, it, Toast.LENGTH_LONG).show()
        }
        viewModel.showToast.observe(this) {
            showToast(it)
        }
        viewModel.showToastInt.observe(this) {
            showToast(it)
        }
        viewModel.showToastIntLong.observe(this) {
            showToastLong(it)
        }
        viewModel.showSnackBar.observe(this) {
            Snackbar.make(this.requireView(), it, Snackbar.LENGTH_LONG).show()
        }
        viewModel.showSnackBarInt.observe(this) {
            Snackbar.make(this.requireView(), getString(it), Snackbar.LENGTH_LONG).show()
        }

        viewModel.navigationCommand.observe(this) { command ->
            if (!isNavigating) {
                isNavigating = true
                when (command) {
                    is NavigationCommand.To -> findNavController().navigate(command.directions)
                    is NavigationCommand.Back -> findNavController().popBackStack()
                    is NavigationCommand.BackTo -> findNavController().popBackStack(
                        command.destinationId,
                        false
                    )
                }
            }
        }
    }

    protected fun showToast(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    protected fun showToast(messageResId: Int) {
        Toast.makeText(activity, getString(messageResId), Toast.LENGTH_SHORT).show()
    }

    protected fun showToastLong(messageResId: Int) {
        Toast.makeText(activity, getString(messageResId), Toast.LENGTH_LONG).show()
    }

    protected fun showSnackBar(message: String) {
        Snackbar.make(this.requireView(), message, Snackbar.LENGTH_LONG).show()
    }

    protected fun showSnackBar(messageResId: Int) {
        Snackbar.make(this.requireView(), getString(messageResId), Snackbar.LENGTH_LONG).show()
    }

    protected fun handleError() {
        when {
            viewModel.customErrorMessageResId != null -> {
                showSnackBar(getString(viewModel.customErrorMessageResId ?: R.string.error))
            }

            else -> {
                showSnackBar(getString(R.string.error))
            }
        }
    }

    protected fun hasNetworkConnection(): Boolean {
        val connectivityManager =
            ContextCompat.getSystemService(requireContext(), ConnectivityManager::class.java)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val currentNetwork = connectivityManager?.activeNetwork
            val caps = connectivityManager?.getNetworkCapabilities(currentNetwork)
            caps?.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) == true
        } else {
            val networkInfo = connectivityManager?.activeNetworkInfo
            networkInfo != null && networkInfo.isConnectedOrConnecting
        }
    }

    protected fun hideKeyboard() {
        val manager =
            activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = activity?.currentFocus
        if (view == null) {
            view = View(activity)
        }
        manager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    protected fun navigateTo(navDirections: NavDirections) {
        if (!isNavigating) {
            isNavigating = true
            findNavController().navigate(navDirections)
        }
    }
}