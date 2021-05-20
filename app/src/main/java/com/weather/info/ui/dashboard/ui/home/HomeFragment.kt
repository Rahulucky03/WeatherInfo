package com.weather.info.ui.dashboard.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.*
import android.os.AsyncTask
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import com.weather.info.R
import com.weather.info.WeatherApp
import com.weather.info.base.fragment.BaseFragment
import com.weather.info.databinding.FragmentHomeBinding
import com.weather.info.utils.view.WorkaroundMapFragment
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException
import java.util.*


@AndroidEntryPoint
class HomeFragment : BaseFragment<HomeViewModel, FragmentHomeBinding>(), OnMapReadyCallback {

    private var currentLatLng: LatLng? = null
    lateinit var markerLatLong: LatLng


    private var mLocationPermissionGranted: Boolean = false

    /**
     * Provides access to the Fused Location Provider API.
     */
    private var mFusedLocationClient: FusedLocationProviderClient? = null

    /**
     * Provides access to the Location Settings API.
     */
    private var mSettingsClient: SettingsClient? = null

    /**
     * Callback for Location events.
     */
    private var mLocationCallback: LocationCallback? = null

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    private var mLocationRequest: LocationRequest? = null

    /**
     * Stores the types of location services the client is interested in using. Used for checking
     * settings to determine if the device has optimal location settings.
     */
    private var mLocationSettingsRequest: LocationSettingsRequest? = null

    /**
     * Tracks the status of the location updates request. Value changes when the user presses the
     * Start Updates and Stop Updates buttons.
     */
    private var mRequestingLocationUpdates: Boolean = false


    private lateinit var placesClient: PlacesClient


    override val layoutRes: Int = R.layout.fragment_home

    private val homeViewModel: HomeViewModel by viewModels()

    override fun getViewModel(): HomeViewModel = homeViewModel

    private lateinit var map: GoogleMap
    //private lateinit var locationManager: LocationManager

    companion object {
        private const val MIN_TIME: Long = 400
        private const val MIN_DISTANCE = 1000f

        /**
         * The desired interval for location updates. Inexact. Updates may be more or less frequent.
         */
        private const val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 10000

        /**
         * The fastest rate for active location updates. Exact. Updates will never be more frequent
         * than this value.
         */
        private const val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2
    }

    override fun onResume() {
        super.onResume()
        // Within {@code onPause()}, we remove location updates. Here, we resume receiving
        // location updates if the user has requested them.
        val permissionState =
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )

        if (mRequestingLocationUpdates && permissionState == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates()
        } else if (permissionState != PackageManager.PERMISSION_GRANTED) {
            getLocationPermission()
        }
    }

    override fun onReadyToRender(
        view: View,
        viewModel: HomeViewModel,
        binder: FragmentHomeBinding,
        savedInstanceState: Bundle?
    ) {
        mRequestingLocationUpdates = true
        binder.fab.setOnClickListener {
            val navDirections =
                HomeFragmentDirections.actionNavHomeToWeatherDetailFragment(markerLatLong)
            findNavController().navigate(navDirections)
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        mSettingsClient = LocationServices.getSettingsClient(requireContext())


        initMap()
        createLocationCallback()
        createLocationRequest()
        buildLocationSettingsRequest()

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        mFusedLocationClient?.lastLocation?.addOnSuccessListener {
            map.moveCamera(CameraUpdateFactory.newLatLng(LatLng(it.latitude, it.longitude)))
        }
    }

    private fun initMap() {
        (childFragmentManager.findFragmentById(R.id.map) as WorkaroundMapFragment).getMapAsync(
            this
        )

        Places.initialize(requireContext(), getString(R.string.google_maps_api))

        placesClient = Places.createClient(requireContext())
        val listOf =
            listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS)
        val placeFields = java.util.ArrayList(listOf)
        val findCurrentPlaceRequest = FindCurrentPlaceRequest.newInstance(placeFields)
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mLocationPermissionGranted = true
            placesClient.findCurrentPlace(findCurrentPlaceRequest)
        } else {
            mLocationPermissionGranted = false
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        map = googleMap ?: return
        val padding = resources.getDimension(R.dimen._52dp).toInt()
        map.setPadding(0, 0, 0, padding)

        updateLocationUI()
        map.uiSettings.isZoomControlsEnabled = true

        (childFragmentManager.findFragmentById(R.id.map) as WorkaroundMapFragment)
            .setListener { getDataBinder().scrollView.requestDisallowInterceptTouchEvent(true) }

        map.setOnCameraIdleListener { //get latlng at the center by calling
            markerLatLong = map.cameraPosition.target
            showAddress(getDataBinder().tvAddress, markerLatLong)
        }
    }

    @RequiresPermission(anyOf = ["android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"])
    private fun startLocationUpdates() {
        // Begin by checking if the device has the necessary location settings.
        mSettingsClient?.checkLocationSettings(mLocationSettingsRequest!!)?.addOnSuccessListener {
            Log.d(TAG, "All location settings are satisfied.")
            mFusedLocationClient?.requestLocationUpdates(
                mLocationRequest!!,
                mLocationCallback!!,
                Looper.myLooper()!!
            )

            updateUI()
        }?.addOnFailureListener { e ->
            when ((e as ApiException).statusCode) {
                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                    Log.i(
                        TAG,
                        "Location settings are not satisfied. Attempting to upgrade " +
                                "location settings "
                    )
                    try {
                        // Show the dialog by calling startResolutionForResult(), and check the
                        // result in onActivityResult().
                        val rae = e as ResolvableApiException
                        rae.startResolutionForResult(requireActivity(), 121)
                    } catch (sie: IntentSender.SendIntentException) {
                        Log.i(
                            TAG,
                            "PendingIntent unable to execute request."
                        )
                    }
                }
                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                    val errorMessage = "Location settings are inadequate, and cannot be " +
                            "fixed here. Fix in Settings."
                    Log.e(
                        TAG,
                        errorMessage
                    )
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
                    mRequestingLocationUpdates = false
                }
            }
            updateUI()
        }
    }

    /**
     * Updates all UI fields.
     */
    private fun updateUI() {
        Log.d(TAG, "Update UI")
    }

    private fun updateLocationUI() {
        try {
            if (mLocationPermissionGranted) {
                map.isMyLocationEnabled = true
                map.uiSettings.isMyLocationButtonEnabled = true

                startLocationUpdates()
                // Get the current location of the device and set the position of the map.
                //mMap.getDeviceLocation(this, placesClient, placeFields, mMarker, this)
            } else {
                map.isMyLocationEnabled = false
                map.uiSettings.isMyLocationButtonEnabled = false
                getLocationPermission()
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message!!)
        }
    }

    private fun getLocationPermission() {
        Permissions.check(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION,
            null,
            object : PermissionHandler() {
                override fun onGranted() {
                    mLocationPermissionGranted = true
                    updateLocationUI()
                }
            })
    }

    /**
     * Creates a callback for receiving location events.
     */
    private fun createLocationCallback() {
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                val currentLocation = locationResult.lastLocation
                currentLatLng = LatLng(currentLocation.latitude, currentLocation.longitude)
                updateLocationUI()
            }
        }
    }

    /**
     * Sets up the location request. Android has two location request settings:
     * `ACCESS_COARSE_LOCATION` and `ACCESS_FINE_LOCATION`. These settings control
     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     *
     *
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     *
     *
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     */
    private fun createLocationRequest() {
        mLocationRequest = LocationRequest.create()

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest?.interval = UPDATE_INTERVAL_IN_MILLISECONDS

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest?.fastestInterval = FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
        mLocationRequest?.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }


    /**
     * Uses a [com.google.android.gms.location.LocationSettingsRequest.Builder] to build
     * a [com.google.android.gms.location.LocationSettingsRequest] that is used for checking
     * if a device has the needed location settings.
     */
    private fun buildLocationSettingsRequest() {
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest!!)
        mLocationSettingsRequest = builder.build()
    }


    @SuppressLint("StaticFieldLeak")
    private fun showAddress(textView: TextView, latLng: LatLng) {
        object : AsyncTask<LatLng, Void, String>() {
            override fun doInBackground(vararg params: LatLng): String {
                try {
                    val addresses: List<Address>
                    val geoCoder = Geocoder(WeatherApp.instance, Locale.getDefault())
                    addresses = geoCoder.getFromLocation(
                        params[0].latitude,
                        params[0].longitude,
                        1
                    ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    return if (addresses.isNotEmpty()) {
                        addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    } else {
                        ""
                    }
                } catch (ex: IOException) {
                    return ""
                }
            }

            override fun onPostExecute(address: String) {
                // do whatever you want/need to do with the address found
                // remember to check first that it's not null
                requireActivity().runOnUiThread {
                    textView.text = address

                    if (currentLatLng != null) {
                        //getDataBinder().tvDistance.text = currentLatLong.distanceTo(markerLatLong!!)
                    }

                }
            }
        }.execute(latLng)
    }

}