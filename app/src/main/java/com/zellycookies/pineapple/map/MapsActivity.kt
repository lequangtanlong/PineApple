package com.zellycookies.pineapple.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.zellycookies.pineapple.R
import com.zellycookies.pineapple.utils.GPS

class MapsActivity : AppCompatActivity(), OnMapReadyCallback  {

    private var mMap: GoogleMap? = null
    var location:Location?=null
    var latitude=10.79474099959847
    var longitude=106.70861138817237
    lateinit var user: FirebaseUser
    private lateinit var gps: GPS
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google_map)
        user = FirebaseAuth.getInstance().currentUser!!
        Log.i("USERRRRRR", user.toString())
        location= Location("Start")
        location!!.latitude = latitude
        location!!.longitude = longitude

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_Fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        gps = GPS(this)

        checkPermmison()
        LoadPockemon()
    }

    var ACCESSLOCATION=123

    fun checkPermmison(){
        if(Build.VERSION.SDK_INT>=23){
            if(ActivityCompat.
                    checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){

                requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), ACCESSLOCATION)
                return
            }
        }

        GetUserLocation()
    }

    fun GetUserLocation(){
        Toast.makeText(this,"User location access on",Toast.LENGTH_LONG).show()
        var myLocation= MylocationListener()

        var locationManager=getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,3,3f,myLocation)

//        var mythread=myThread()
//        mythread.start()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        when(requestCode){

            ACCESSLOCATION->{

                if (grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    GetUserLocation()
                }else{
                    Toast.makeText(this,"We cannot access to your location",Toast.LENGTH_LONG).show()
                }
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        Log.i("LOCATIONNNNNNNNNNNNNNN", location.toString())
        drawUser(latitude, longitude)
    }



    //Get user location
    fun drawUser(latitude: Double, longitude: Double){
        if (mMap != null) {
            val me = LatLng(latitude, longitude)
            mMap!!.addMarker(MarkerOptions()
                .position(me)
                .title("Me")
                .snippet(" here is my location")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.charmander)))
            mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(me, 14f))
        }

    }

    inner class MylocationListener: LocationListener {
        constructor(){

        }

        override fun onLocationChanged(p0: Location) {
//             location=p0
//        drawUser(location)
            Log.i("LOCATIONNNNNNNNNNNNNNN", location.toString())
        }
//
//        override fun onLocationChanged(p0: Location) {
//            //TODO("Not yet implemented")
//        }
//
//        override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
//            //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//        }
//
//        override fun onProviderEnabled(p0: String?) {
//           // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//        }
//
//        override fun onProviderDisabled(p0: String?) {
//            //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//        }

    }




    var listPockemons=ArrayList<Pockemon>()

    fun  LoadPockemon(){


//        listPockemons.add(Pockemon(R.drawable.charmander,
//                "Charmander", "Charmander living in japan", 55.0, 37.7789994893035, -122.401846647263))
//        listPockemons.add(Pockemon(R.drawable.bulbasaur,
//                "Bulbasaur", "Bulbasaur living in usa", 90.5, 37.7949568502667, -122.410494089127))
//        listPockemons.add(Pockemon(R.drawable.squirtle,
//                "Squirtle", "Squirtle living in iraq", 33.5, 37.7816621152613, -122.41225361824))

    }

}
