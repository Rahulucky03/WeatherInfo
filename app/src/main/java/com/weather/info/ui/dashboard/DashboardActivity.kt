package com.weather.info.ui.dashboard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.core.view.GravityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.weather.info.R
import com.weather.info.base.activity.BaseActivity
import com.weather.info.databinding.ActivityDashboardBinding
import com.weather.info.databinding.NavHeaderMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardActivity : BaseActivity<DashboardViewModel, ActivityDashboardBinding>() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    override val layoutRes: Int = R.layout.activity_dashboard

    private val dashboardViewModel: DashboardViewModel by viewModels()

    override fun getViewModel(): DashboardViewModel = dashboardViewModel

    companion object {
        fun startInstanceWithBackStackCleared(context: Context?) {
            val intent = Intent(context, DashboardActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            context?.startActivity(intent)
        }
    }

    override fun onReadyToRender(
        viewModel: DashboardViewModel,
        binder: ActivityDashboardBinding,
        savedInstanceState: Bundle?
    ) {
        setSupportActionBar(binder.toolbar)

        binder.viewModel = viewModel

        val navBinding = NavHeaderMainBinding.bind(binder.navView.getHeaderView(0))
        navBinding.viewModel = viewModel

        val navController = findNavController(R.id.nav_host_fragment)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_history
            ), binder.drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        binder.navView.setupWithNavController(navController)

        binder.navView.menu.findItem(R.id.nav_Logout)?.let {
            it.setOnMenuItemClickListener {
                closeDrawer()
                if (it.itemId == R.id.nav_Logout) {
                    getViewModel().logoutUser()
                }
                true
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.dashboard, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_settings) {
            getViewModel().normal.value = "Setting"
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun toggleDrawer() {
        if (isDrawerOpen()) {
            closeDrawer()
        } else {
            openDrawer()
        }
    }

    private fun isDrawerOpen(): Boolean {
        return binder.drawerLayout.isDrawerOpen(GravityCompat.START)
    }

    private fun openDrawer() {
        binder.drawerLayout.openDrawer(GravityCompat.START)
    }

    private fun closeDrawer() {
        binder.drawerLayout.closeDrawer(GravityCompat.START)
    }

}