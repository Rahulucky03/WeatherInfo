package com.weather.info.ui.splash

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.viewModels
import com.weather.info.R
import com.weather.info.base.activity.BaseActivity
import com.weather.info.data.enums.AppFlow
import com.weather.info.databinding.FragmentSplashBinding
import com.weather.info.ui.dashboard.DashboardActivity
import com.weather.info.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SplashActivity : BaseActivity<SplashViewModel, FragmentSplashBinding>() {

    private val mViewModel: SplashViewModel by viewModels()

    override fun getViewModel(): SplashViewModel = mViewModel

    override val layoutRes: Int = R.layout.fragment_splash

    companion object {
        const val SPLASH_TIMER = 2000L //2 Sec
    }

    override fun onReadyToRender(
        viewModel: SplashViewModel,
        binder: FragmentSplashBinding,
        savedInstanceState: Bundle?
    ) {
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

        Handler(Looper.getMainLooper()).postDelayed({
            viewModel.launchNextScreen()
        }, SPLASH_TIMER)

    }

    override fun subscribeObserver() {
        super.subscribeObserver()
        getViewModel().appFlow.observe(this, {
            it?.let {
                when (it) {
                    AppFlow.GO_TO_DASHBOARD -> {
                        DashboardActivity.startInstanceWithBackStackCleared(this)
                    }
                    AppFlow.GO_TO_AUTHENTICATION -> {
                        LoginActivity.startInstanceWithBackStackCleared(this)
                    }
                    else -> {

                    }
                }
            }
        })
    }
}