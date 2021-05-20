package com.weather.info.base.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.weather.info.base.BaseActivity
import com.weather.info.base.progress.ProgressBarManager
import com.weather.info.base.viewmodel.BaseViewModel
import com.weather.info.data.AppDataManager
import com.weather.info.ui.login.LoginActivity
import io.github.tonnyl.light.*
import javax.inject.Inject

abstract class BaseFragment<VM : BaseViewModel, T : ViewDataBinding> : Fragment() {
    protected var TAG: String = this.javaClass.simpleName
    private var mActivity: BaseActivity<VM, T>? = null

    private var binder: T? = null
    private lateinit var mViewModel: VM

    private var rootView: View? = null
    private var isFragmentLoaded = false

    @get:LayoutRes
    abstract val layoutRes: Int

    abstract fun getViewModel(): VM

    fun getDataBinder(): T {
        return this.binder!!
    }

    lateinit var progressListener: ProgressBarManager

    @Inject
    protected lateinit var appDataManager: AppDataManager

    override fun onAttach(context: Context) {
        try {
            super.onAttach(context)
            if (context is BaseActivity<*, *>) {
                val activity = context as BaseActivity<VM, T>?
                this.mActivity = activity
                //activity?.onFragmentAttached()
            }
        } catch (e: Throwable) {
            throw ClassCastException("$context must implement FragmentListener")
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = getViewModel()
        setHasOptionsMenu(false)
        subscribeObserver(mViewModel)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binder = DataBindingUtil.inflate(inflater, layoutRes, container, false)!!
        binder?.lifecycleOwner = viewLifecycleOwner
        rootView = binder?.root!!
        return rootView!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            mViewModel.let { vm ->
                binder?.let { binder ->
                    progressListener = ProgressBarManager(getProgressBar())
                    //changeStatusBarColor(isTransparent())
                    onReadyToRender(view, vm, binder, savedInstanceState)
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    protected open fun changeStatusBarColor(isTransparent: Boolean) {
        /*if (isTransparent) {

        } else {

        }*/
    }

    @CallSuper
    open fun subscribeObserver(viewModel: VM) {
        viewModel.success.observe(this) {
            if (requireView().isVisible) {
                if (it != null && it.isNotEmpty()) {
                    success(rootView ?: getDataBinder().root, it, Snackbar.LENGTH_SHORT).show()
                    viewModel.success.value = null
                }
            }
        }
        viewModel.info.observe(this) {
            if (requireView().isVisible) {
                if (it != null && it.isNotEmpty()) {
                    info(rootView ?: getDataBinder().root, it, Snackbar.LENGTH_SHORT).show()
                    viewModel.info.value = null
                }
            }
        }
        viewModel.normal.observe(this) {
            if (requireView().isVisible) {
                if (it != null && it.isNotEmpty()) {
                    normal(rootView ?: getDataBinder().root, it, Snackbar.LENGTH_SHORT).show()
                    viewModel.normal.value = null
                }
            }
        }
        viewModel.warning.observe(this) {
            if (requireView().isVisible) {
                if (it != null && it.isNotEmpty()) {
                    warning(rootView ?: getDataBinder().root, it, Snackbar.LENGTH_SHORT).show()
                    viewModel.warning.value = null
                }
            }
        }
        viewModel.error.observe(this) {
            if (requireView().isVisible) {
                if (it != null && it.isNotEmpty()) {
                    error(rootView ?: getDataBinder().root, it, Snackbar.LENGTH_SHORT).show()
                    viewModel.error.value = null
                }
            }
        }
        viewModel.unAuthorizeError.observe(this) {
            if (requireView().isVisible) {
                if (it != null && it.isNotEmpty()) {
                    error(rootView ?: getDataBinder().root, it, Snackbar.LENGTH_SHORT)
                        .setAction("Sign Out") {
                            viewModel.logoutUser()
                        }
                        .show()
                    viewModel.unAuthorizeError.value = null
                }
            }
        }

        viewModel.logoutSuccess.observe(this) {
            if (requireView().isVisible) {
                if (it != null && it == true) {
                    LoginActivity.startInstanceWithBackStackCleared(requireContext())
                    viewModel.logoutSuccess.value = null
                }
            }
        }





        viewModel.loading.observe(this) {
            if (requireView().isVisible) {
                progressListener.showLoading(it)
            }
        }

        viewModel.isServerNotAvailable.observe(this) {
            if (requireView().isVisible) {
                Snackbar.make(requireView(), "Server not running", Snackbar.LENGTH_SHORT).show()
            }
        }

        viewModel.isTokenExpired.observe(this) {
            if (requireView().isVisible) {

            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        binder = null
    }

    override fun onDetach() {
        super.onDetach()
        this.mActivity = null
    }

    override fun onDestroy() {
        super.onDestroy()
        isFragmentLoaded = false
    }

    fun getBaseActivity(): BaseActivity<VM, T>? {
        return this.mActivity
    }

    protected abstract fun onReadyToRender(
        view: View,
        viewModel: VM,
        binder: T,
        savedInstanceState: Bundle?
    )

    /*protected abstract fun isTransparent(): Boolean*/

    fun launchOnLifecycleScope(execute: suspend () -> Unit) {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            execute()
        }
    }

    open fun getProgressBar(): ProgressBar? {
        return mActivity?.getProgressBar()
    }

}
