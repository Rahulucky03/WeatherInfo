package com.weather.info.ui.dashboard.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.weather.info.base.viewmodel.BaseViewModel
import com.weather.info.data.AppDataManager
import com.weather.info.data.firebase.FireBaseAuthProvider
import com.weather.info.data.room.entity.History
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    override val appDataManager: AppDataManager,
    override val fireBaseAuthProvider: FireBaseAuthProvider
) : BaseViewModel(appDataManager, fireBaseAuthProvider) {

    val moveToDetails = MutableLiveData<Boolean>()

    fun saveLocationInRoom(markerLatLong: LatLng, address: String) =
        viewModelScope.launch(Dispatchers.Main) {
            appDataManager.saveHistory(History(0, markerLatLong, address))
            moveToDetails.value = true
        }

}