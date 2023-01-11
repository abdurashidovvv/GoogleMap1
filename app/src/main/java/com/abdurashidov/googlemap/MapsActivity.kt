package com.abdurashidov.googlemap

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.abdurashidov.googlemap.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap.OnPolygonClickListener
import com.google.android.gms.maps.GoogleMap.OnPolylineClickListener
import com.google.android.gms.maps.model.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, OnPolylineClickListener,
    OnPolygonClickListener {

    private lateinit var binding: ActivityMapsBinding
    private  val TAG = "MapsActivity"

    private lateinit var map: GoogleMap

    lateinit var fusedLocationProviderClient:FusedLocationProviderClient

    var locationPermissionGranted=false
    val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION=1


    var lastKnownLocation:Location?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient=LocationServices.getFusedLocationProviderClient(this)
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
//        map = googleMap

        // Add a marker in Sydney and move the camera
//        val codial = LatLng(40.38303230737299, 71.78269947088963)
//        map.addMarker(MarkerOptions().position(codial).title("Marker in Codial"))
//        map.moveCamera(CameraUpdateFactory.newLatLng(codial))


        // Add polylines to the map.
        // Polylines are useful to show a route or some other connection between points.
//        val polyline1 = googleMap.addPolyline(
//            PolylineOptions()
//            .clickable(true)
//            .add(
//                LatLng(-35.016, 143.321),
//                LatLng(-34.747, 145.592),
//                LatLng(-34.364, 147.891),
//                LatLng(-33.501, 150.217),
//                LatLng(-32.306, 149.248),
//                LatLng(-32.491, 147.309)))

        // Position the map's camera near Alice Springs in the center of Australia,
        // and set the zoom factor so most of Australia shows on the screen.
//        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(-23.684, 133.903), 4f))

        // Set listeners for click events.
//        googleMap.setOnPolylineClickListener(this)

//        val polygon1 = googleMap.addPolygon(PolygonOptions()
//            .clickable(true)
//            .add(
//                LatLng(-27.457, 153.040),
//                LatLng(-33.852, 151.211),
//                LatLng(-37.813, 144.962),
//                LatLng(-34.928, 138.599)))
//// Store a data object with the polygon, used here to indicate an arbitrary type.
//        polygon1.tag = "alpha"
//// Style the polygon.
//        googleMap.setOnPolygonClickListener(this)


        this.map=googleMap
        updateLocationUI()
        getDeviceLocation()




    }

    override fun onPolylineClick(p0: Polyline) {
//        Toast.makeText(this, "${p0.points}", Toast.LENGTH_SHORT).show()
    }

    override fun onPolygonClick(p0: Polygon) {
        Toast.makeText(this, "${p0.points[0].latitude} : ${p0.points[0].longitude}", Toast.LENGTH_SHORT).show()
    }

    private fun getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        locationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
        updateLocationUI()
    }

    private fun updateLocationUI() {
        if (map == null) {
            return
        }
        try {
            if (locationPermissionGranted) {
                map.isMyLocationEnabled = true
                map.uiSettings.isMyLocationButtonEnabled = true
            } else {
                map.isMyLocationEnabled = false
                map.uiSettings.isMyLocationButtonEnabled = false
                lastKnownLocation = null
                getLocationPermission()
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    private fun getDeviceLocation() {
        /*
   * Get the best and most recent location of the device, which may be null in rare
   * cases when a location is not available.
   */
        try {
            if (locationPermissionGranted) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Set the map's camera position to the current location of the device.
                        lastKnownLocation = task.result
                        if (lastKnownLocation != null) {
                            map.addMarker(
                                MarkerOptions().position(
                                    LatLng(lastKnownLocation!!.latitude,
                                        lastKnownLocation!!.longitude)
                                )
                            )
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                LatLng(lastKnownLocation!!.latitude,
                                    lastKnownLocation!!.longitude), 10f))
                        }
                        map.isMyLocationEnabled=true
                        map.uiSettings.isMyLocationButtonEnabled = true

                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.")
                        Log.e(TAG, "Exception: %s", task.exception)
                        map.moveCamera(CameraUpdateFactory
                            .newLatLngZoom(LatLng(40.38303230737299, 71.78269947088963), 10f))
                        map.uiSettings.isMyLocationButtonEnabled = false
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }


}