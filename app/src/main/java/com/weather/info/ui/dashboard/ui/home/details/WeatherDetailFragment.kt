package com.weather.info.ui.dashboard.ui.home.details

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.weather.info.R
import com.weather.info.base.fragment.BaseFragment
import com.weather.info.data.room.entity.History
import com.weather.info.databinding.FragmentWeatherDetailBinding
import com.weather.info.ui.dashboard.ui.home.details.child.WeatherChildFragment
import com.weather.info.utils.anim.DepthPageTransformer
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WeatherDetailFragment : BaseFragment<WeatherDetailViewModel, FragmentWeatherDetailBinding>() {

    override val layoutRes: Int = R.layout.fragment_weather_detail

    private val weatherViewModel: WeatherDetailViewModel by viewModels()

    var history: History? = null

    override fun getViewModel(): WeatherDetailViewModel = weatherViewModel

    override fun onReadyToRender(
        view: View,
        viewModel: WeatherDetailViewModel,
        binder: FragmentWeatherDetailBinding,
        savedInstanceState: Bundle?
    ) {

        history = WeatherDetailFragmentArgs.fromBundle(requireArguments()).history

        binder.viewpager.adapter = ScreenSlidePagerAdapter(this)
        binder.viewpager.setPageTransformer(DepthPageTransformer())

        TabLayoutMediator(binder.tab, binder.viewpager) { tab, position ->
            tab.text = if (position == 0) "Current Weather" else "5 Days Forecast"
            binder.viewpager.setCurrentItem(tab.position, true)
        }.attach()


    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private inner class ScreenSlidePagerAdapter(fa: Fragment) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            val fragment = WeatherChildFragment()
            val bundle = Bundle()
            bundle.putInt("position", position)
            bundle.putParcelable("history", history)
            fragment.arguments = bundle
            return fragment
        }
    }


}