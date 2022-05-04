package com.zellycookies.pineapple.utils

import android.Manifest
import android.os.Bundle
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat


import android.content.Context
import android.location.LocationListener
import android.location.LocationManager
import android.location.Location
import android.util.Log


class GPS(var mContext: Context) : LocationListener {
    var mlocation: Location? = null
    var mLocationManager: LocationManager = mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    var mProvider = LocationManager.GPS_PROVIDER

    init {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Return
        } else {
            mlocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            //onLocationChanged(mlocation!!)
            onChangedLocation(mlocation)
        }
    }

    fun onChangedLocation(location: Location?) {
        this.mlocation = location
    }

    override fun onLocationChanged(location: Location) {
        this.mlocation = location
        Log.i("LOCATIONNNNNNNNNNNNNNN", location.toString())

    }

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
    override fun onProviderEnabled(provider: String) {}
    override fun onProviderDisabled(provider: String) {}
    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Int {
        val theta = lon1 - lon2
        var dist =
            Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(
                deg2rad(lat2)
            ) * Math.cos(deg2rad(theta))
        dist = Math.acos(dist)
        dist = rad2deg(dist)
        dist *= 60 * 1.1515
        val dis = Math.floor(dist).toInt()
        return if (dis < 1) {
            1
        } else dis
    }

    private fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180.0
    }

    private fun rad2deg(rad: Double): Double {
        return rad * 180 / Math.PI
    }


}