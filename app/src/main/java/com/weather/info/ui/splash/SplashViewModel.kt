package com.weather.info.ui.splash

import androidx.lifecycle.MutableLiveData
import com.weather.info.base.viewmodel.BaseViewModel
import com.weather.info.data.AppDataManager
import com.weather.info.data.enums.AppFlow
import com.weather.info.data.firebase.FireBaseAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    override val appDataManager: AppDataManager,
    override val fireBaseAuthProvider: FireBaseAuthProvider
) :
    BaseViewModel(appDataManager, fireBaseAuthProvider) {

    val appFlow = MutableLiveData<AppFlow>()

    fun launchNextScreen() {
        if (fireBaseAuthProvider.isUserVerified()) {
            appFlow.value = AppFlow.GO_TO_DASHBOARD
        } else {
            appFlow.value = AppFlow.GO_TO_AUTHENTICATION
        }
    }
}