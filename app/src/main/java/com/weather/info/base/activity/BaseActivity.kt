package com.weather.info.base.activity

import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.google.android.material.snackbar.Snackbar
import com.weather.info.R
import com.weather.info.base.progress.ProgressBarManager
import com.weather.info.base.viewmodel.BaseViewModel
import com.weather.info.data.AppDataManager
import com.weather.info.ui.login.LoginActivity
import io.github.tonnyl.light.info
import io.github.tonnyl.light.normal
import io.github.tonnyl.light.success
import io.github.tonnyl.light.warning
import javax.inject.Inject

abstract class BaseActivity<VM : BaseViewModel, T : ViewDataBinding> : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar

    private lateinit var mViewModel: VM
    lateinit var binder: T

    @get:LayoutRes
    abstract val layoutRes: Int

    lateinit var progressListener: ProgressBarManager

    abstract fun getViewModel(): VM

    @Inject
    protected lateinit var appDataManager: AppDataManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = getViewModel()
        setContentView(layoutRes)
    }

    override fun setContentView(layoutRes: Int) {
        val coordinatorLayout: CoordinatorLayout =
            layoutInflater.inflate(R.layout.activity_base, null) as CoordinatorLayout
        val activityContainer: FrameLayout = coordinatorLayout.findViewById(R.id.layout_container)
        progressBar = coordinatorLayout.findViewById(R.id.progressBar) as ProgressBar
        binder = DataBindingUtil.inflate(layoutInflater, layoutRes, activityContainer, true)
        binder.lifecycleOwner = this
        super.setContentView(coordinatorLayout)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        onReadyToRender(mViewModel, binder, savedInstanceState)
        progressListener = ProgressBarManager(getProgressBar())
        subscribeObserver()
    }

    @CallSuper
    open fun subscribeObserver() {
        mViewModel.success.observe(this) {
            if (it != null && it.isNotEmpty()) {
                success(binder.root, it, Snackbar.LENGTH_SHORT).show()
                mViewModel.success.value = null
            }
        }
        mViewModel.info.observe(this) {
            if (it != null && it.isNotEmpty()) {
                info(binder.root, it, Snackbar.LENGTH_SHORT).show()
                mViewModel.info.value = null
            }

        }
        mViewModel.normal.observe(this) {
            if (it != null && it.isNotEmpty()) {
                normal(binder.root, it, Snackbar.LENGTH_SHORT).show()
                mViewModel.normal.value = null
            }
        }
        mViewModel.warning.observe(this) {
            if (it != null && it.isNotEmpty()) {
                warning(binder.root, it, Snackbar.LENGTH_SHORT).show()
                mViewModel.warning.value = null
            }
        }
        mViewModel.error.observe(this) {
            if (it != null && it.isNotEmpty()) {
                io.github.tonnyl.light.error(
                    binder.root,
                    it,
                    Snackbar.LENGTH_SHORT
                ).show()
                mViewModel.error.value = null
            }
        }
        mViewModel.unAuthorizeError.observe(this) {
            if (it != null && it.isNotEmpty()) {
                io.github.tonnyl.light.error(
                    binder.root,
                    it,
                    Snackbar.LENGTH_SHORT
                )
                    .setAction("Sign Out") {
                        mViewModel.logoutUser()
                    }
                    .show()
                mViewModel.unAuthorizeError.value = null
            }
        }

        mViewModel.logoutSuccess.observe(this) {
            if (it != null && it == true) {
                LoginActivity.startInstanceWithBackStackCleared(this)
                mViewModel.logoutSuccess.value = null
            }
        }

        mViewModel.loading.observe(this, {
            progressListener.showLoading(it)
        })

        mViewModel.isServerNotAvailable.observe(this, {
            Snackbar.make(
                findViewById(android.R.id.content)!!,
                "Server not running",
                Snackbar.LENGTH_SHORT
            ).show()
        })

        mViewModel.isTokenExpired.observe(this, {

        })
    }

    protected abstract fun onReadyToRender(viewModel: VM, binder: T, savedInstanceState: Bundle?)

    /**
     *  open it for overload it in other child activity
     *  So in case
     *  If you want to use BaseProgress just ignore it, Don't need to override this fun
     *  If you want to your progressbar change, Just define in your xml and override the progress bar instance to base
     *  If you don't want to use Base as well any custom progress just override this and return null
     *  */
    open fun getProgressBar(): ProgressBar? {
        return progressBar
    }

}
