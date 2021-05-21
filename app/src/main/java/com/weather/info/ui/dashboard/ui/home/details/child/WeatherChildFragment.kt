package com.weather.info.ui.dashboard.ui.home.details.child

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.weather.info.R
import com.weather.info.base.fragment.BaseFragment
import com.weather.info.data.room.entity.History
import com.weather.info.databinding.FragmentWeatherChildBinding
import com.weather.info.ui.dashboard.ui.home.details.child.adapter.WeatherAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WeatherChildFragment : BaseFragment<WeatherChildViewModel, FragmentWeatherChildBinding>() {

    private lateinit var weatherAdapter: WeatherAdapter
    override val layoutRes: Int = R.layout.fragment_weather_child

    private val weatherViewModel: WeatherChildViewModel by viewModels()

    override fun getViewModel(): WeatherChildViewModel = weatherViewModel

    override fun onReadyToRender(
        view: View,
        viewModel: WeatherChildViewModel,
        binder: FragmentWeatherChildBinding,
        savedInstanceState: Bundle?
    ) {
        binder.viewModel = viewModel

        val position = requireArguments().getInt("position", 0)
        if (position == 0) {
            binder.currentWeatherLayout.visibility = View.VISIBLE
            binder.daysWeatherLayout.visibility = View.GONE
        } else {
            binder.daysWeatherLayout.visibility = View.VISIBLE
            binder.currentWeatherLayout.visibility = View.GONE
        }


        with(binder.includedRecycler.recyclerView) {
            weatherAdapter = WeatherAdapter()
            val linearLayoutManager = LinearLayoutManager(requireContext())
            layoutManager = linearLayoutManager
            adapter = weatherAdapter
        }

        binder.includedRecycler.swipeLayout.setOnRefreshListener {
            getMultipleDayWeather()
        }

    }

    fun getMultipleDayWeather() {
        val history = requireArguments().getParcelable("history") as History?
        getViewModel().getWeather(history)
    }

    //For refreshing
    override fun onResume() {
        super.onResume()
        getMultipleDayWeather()
    }

    override fun subscribeObserver(viewModel: WeatherChildViewModel) {
        super.subscribeObserver(viewModel)
        viewModel.weeklyWeather.observe(this) {
            getDataBinder().includedRecycler.swipeLayout.isRefreshing = false
            if (it != null && it.isNotEmpty()) {
                getDataBinder().includedRecycler.tvNoDataFound.visibility = View.GONE
                weatherAdapter.clear()
                for (item in it) {
                    item?.let {
                        weatherAdapter.add(item.toString())
                    }
                }
            } else {
                getDataBinder().includedRecycler.tvNoDataFound.visibility = View.VISIBLE
            }
        }
    }

}