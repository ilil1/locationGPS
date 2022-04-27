package com.project.locationgps

import android.Manifest
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.project.locationgps.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var locationManager: LocationManager
    private lateinit var myLocationListener: MyLocationListener
    private lateinit var binding: ActivityMainBinding

    companion object {
        val locationPermissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val responsePermissions = permissions.entries.filter {
                it.key in locationPermissions //일치성 여부 확인.
            }

            if (responsePermissions.filter { it.value == true }.size == locationPermissions.size) {
                setLocationListener()
            } else {
                Toast.makeText(this, "no", Toast.LENGTH_SHORT).show()
            }
        }

    private fun getMylocation() {
        if (::locationManager.isInitialized.not()) {
            locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }
        val isGpsEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (isGpsEnable) {
            permissionLauncher.launch(locationPermissions)
        }
    }

    @Suppress("MissingPermission")
    private fun setLocationListener() {
        val minTime: Long = 1500
        val minDistance = 100f

        if (::myLocationListener.isInitialized.not()) {
            myLocationListener = MyLocationListener()
        }

        with(locationManager) {
            requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                minTime, minDistance, myLocationListener
            )

            requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                minTime, minDistance, myLocationListener
            )
        }
    }

    inner class MyLocationListener : LocationListener {
        override fun onLocationChanged(location: Location) {
            Toast
                .makeText(this@MainActivity, "${location.latitude}, ${location.longitude}", Toast.LENGTH_SHORT)
                .show()

            binding.locationTitleTextView.text = "${location.latitude}, ${location.longitude}"

            removeLocationListener()
        }

        private fun removeLocationListener() {
            if (::locationManager.isInitialized && ::myLocationListener.isInitialized) {
                locationManager.removeUpdates(myLocationListener)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater); // 1
        setContentView(binding.root)
        getMylocation()
    }
}