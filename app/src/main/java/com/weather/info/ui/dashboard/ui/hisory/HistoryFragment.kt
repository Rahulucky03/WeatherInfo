package com.weather.info.ui.dashboard.ui.hisory

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.weather.info.R
import com.weather.info.base.fragment.BaseFragment
import com.weather.info.data.room.entity.History
import com.weather.info.databinding.FragmentHistoryBinding
import com.weather.info.ui.dashboard.ui.hisory.adapter.HistoryAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryFragment : BaseFragment<HistoryViewModel, FragmentHistoryBinding>(),
    HistoryAdapter.HistoryListener {

    private lateinit var historyAdapter: HistoryAdapter
    private val historyViewModel: HistoryViewModel by viewModels()

    override val layoutRes: Int = R.layout.fragment_history

    override fun getViewModel(): HistoryViewModel = historyViewModel

    override fun onReadyToRender(
        view: View,
        viewModel: HistoryViewModel,
        binder: FragmentHistoryBinding,
        savedInstanceState: Bundle?
    ) {


        with(binder.includedRecycler.recyclerView) {
            historyAdapter = HistoryAdapter(this@HistoryFragment)
            val linearLayoutManager = LinearLayoutManager(requireContext())
            layoutManager = linearLayoutManager
            adapter = historyAdapter
        }

        binder.includedRecycler.swipeLayout.setOnRefreshListener {
            getViewModel().getAllHistory()
        }
    }

    override fun subscribeObserver(viewModel: HistoryViewModel) {
        super.subscribeObserver(viewModel)
        viewModel.historyList.observe(this) {
            getDataBinder().includedRecycler.swipeLayout.isRefreshing = false
            if (it != null && it.isNotEmpty()) {
                getDataBinder().includedRecycler.tvNoDataFound.visibility = View.GONE
                historyAdapter.clear()
                historyAdapter.addAll(it)
            } else {
                getDataBinder().includedRecycler.tvNoDataFound.visibility = View.VISIBLE
            }
        }

        viewModel.removeItemPosition.observe(this) {
            if (it != null && it != -1) {
                historyAdapter.remove(it)
                getViewModel().removeItemPosition.value = -1
            }
        }

    }

    override fun onItemClick(view: View, position: Int, history: History) {
        val actionDetail =
            HistoryFragmentDirections.actionNavHistoryToWeatherDetailFragment(history)
        findNavController().navigate(actionDetail)
    }

    override fun onDeleteHistoryItem(view: View, position: Int, history: History) {
        getViewModel().deleteHistoryItem(position, history)
    }
}