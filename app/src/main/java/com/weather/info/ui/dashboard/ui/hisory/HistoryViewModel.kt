package com.weather.info.ui.dashboard.ui.hisory

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.weather.info.base.viewmodel.BaseViewModel
import com.weather.info.data.AppDataManager
import com.weather.info.data.firebase.FireBaseAuthProvider
import com.weather.info.data.room.entity.History
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    override val appDataManager: AppDataManager,
    override val fireBaseAuthProvider: FireBaseAuthProvider
) : BaseViewModel(appDataManager, fireBaseAuthProvider) {

    val historyList = MutableLiveData<List<History>>()
    val removeItemPosition = MutableLiveData<Int>(-1)

    init {
        getAllHistory()
    }

    fun getAllHistory() = viewModelScope.launch(Dispatchers.Main) {
        val history = appDataManager.getAllHistory()
        historyList.postValue(history)
    }

    fun deleteHistoryItem(position: Int, history: History) =
        viewModelScope.launch(Dispatchers.Main) {
            val count = appDataManager.deleteHistory(history)
            if (count == 1) {
                removeItemPosition.postValue(position)
            }
        }

}