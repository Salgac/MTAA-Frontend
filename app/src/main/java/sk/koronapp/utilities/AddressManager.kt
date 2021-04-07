package sk.koronapp.utilities

import android.app.Activity
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import java.util.*

class AddressManager constructor(activity: Activity) {

    private var address: String = "No location available"

    init {
        updateAddress(activity)
    }

    private fun updateAddress(activity: Activity) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)

        //check permissions
        if (ActivityCompat.checkSelfPermission(
                activity,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            //ask for permissions
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                2
            )
            return updateAddress(activity)
        }

        //update location
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            //get current address from location and return in into variable
            val geocoder = Geocoder(activity, Locale.getDefault())
            val addresses = location?.let { geocoder.getFromLocation(it.latitude, it.longitude, 1) }

            address = addresses?.get(0)?.getAddressLine(0).toString()
        }
    }

    fun getAddress(): String {
        return address
    }
}
