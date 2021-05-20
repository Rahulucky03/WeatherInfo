package com.weather.info.ui.dashboard.ui.slideshow

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.weather.info.base.viewmodel.BaseViewModel
import com.weather.info.data.AppDataManager
import com.weather.info.data.firebase.FireBaseAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SlideshowViewModel @Inject constructor(
    override val appDataManager: AppDataManager,
    override val fireBaseAuthProvider: FireBaseAuthProvider
) : BaseViewModel(appDataManager, fireBaseAuthProvider) {

    private val _text = MutableLiveData<String>().apply {
        value = "This is slideshow Fragment"
    }
    val text: LiveData<String> = _text
}