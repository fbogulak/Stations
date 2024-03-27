package com.example.stations.ui.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.stations.utils.LoadingStatus
import com.example.stations.utils.NavigationCommand
import com.example.stations.utils.SingleLiveEvent

/**
 * Base class for View Models to declare the common LiveData objects in one place
 */
abstract class BaseViewModel : ViewModel() {

    val navigationCommand: SingleLiveEvent<NavigationCommand> = SingleLiveEvent()
    val showErrorMessage: SingleLiveEvent<String> = SingleLiveEvent()
    val showSnackBar: SingleLiveEvent<String> = SingleLiveEvent()
    val showSnackBarInt: SingleLiveEvent<Int> = SingleLiveEvent()
    val showToast: SingleLiveEvent<String> = SingleLiveEvent()
    val showToastInt: SingleLiveEvent<Int> = SingleLiveEvent()
    val showToastIntLong: SingleLiveEvent<Int> = SingleLiveEvent()

    protected val mutableStatus = MutableLiveData<LoadingStatus>()
    val status: LiveData<LoadingStatus>
        get() = mutableStatus

    private var _errorMessage: String? = null
    val errorMessage
        get() = _errorMessage

    private var _customErrorMessageResId: Int? = null
    val customErrorMessageResId
        get() = _customErrorMessageResId

    private val mutableLoadingMessageInt = MutableLiveData<Int?>()
    val loadingMessageInt: LiveData<Int?>
        get() = mutableLoadingMessageInt

    fun showToast(message: String) {
        showToast.value = message
    }

    fun showToast(messageResId: Int) {
        showToastInt.value = messageResId
    }

    fun showToastLong(messageResId: Int) {
        showToastIntLong.value = messageResId
    }

    fun setHasNoInternet() {
        mutableStatus.value = LoadingStatus.NO_INTERNET
    }

    protected fun setError(errorMessage: String?) {
        _errorMessage = errorMessage
        mutableStatus.value = LoadingStatus.ERROR
    }

    fun setError(customErrorMessageResId: Int) {
        _customErrorMessageResId = customErrorMessageResId
        mutableStatus.value = LoadingStatus.ERROR
    }

    fun setError(errorMessage: String?, customErrorMessageResId: Int) {
        _errorMessage = errorMessage
        _customErrorMessageResId = customErrorMessageResId
        mutableStatus.value = LoadingStatus.ERROR
    }

    fun setLoadingMessage(loadingMessageResId: Int) {
        mutableLoadingMessageInt.value = loadingMessageResId
        mutableStatus.value = LoadingStatus.LOADING
    }
}