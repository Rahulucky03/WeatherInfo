package com.weather.info.ui.dashboard.ui.home.details.child

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.model.LatLng
import com.weather.info.R
import com.weather.info.base.fragment.BaseFragment
import com.weather.info.databinding.FragmentWeatherDetailBinding
import com.weather.info.ui.dashboard.ui.home.details.WeatherDetailViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WeatherChildFragment : BaseFragment<WeatherDetailViewModel, FragmentWeatherDetailBinding>() {

    override val layoutRes: Int = R.layout.fragment_weather_detail

    private val weatherViewModel: WeatherDetailViewModel by viewModels()

    override fun getViewModel(): WeatherDetailViewModel = weatherViewModel

    override fun onReadyToRender(
        view: View,
        viewModel: WeatherDetailViewModel,
        binder: FragmentWeatherDetailBinding,
        savedInstanceState: Bundle?
    ) {

    }

    //For refreshing
    override fun onResume() {
        super.onResume()
        val position = requireArguments().getInt("position", 0)
        val latLng = requireArguments().getParcelable("latLng") as LatLng?

        getViewModel().getWeather(latLng)

        //getViewModel().success.value = "$position -> ${latLng?.latitude}, ${latLng?.longitude}"
    }

}