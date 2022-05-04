package com.zellycookies.pineapple.fire

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.util.Util
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.zellycookies.pineapple.R
import com.zellycookies.pineapple.profile.Profile_Activity
import com.zellycookies.pineapple.utils.GPS
import com.zellycookies.pineapple.utils.TopNavigationViewHelper
import com.zellycookies.pineapple.utils.User

class FireHotTakesActivity : AppCompatActivity(), OnMapReadyCallback {

    private var tabLayout : TabLayout? = null

    private val mContext: Context = this@FireHotTakesActivity

    private var mMap: GoogleMap? = null
    var location: Location?=null
    var latitude=10.79474099959847
    var longitude=106.70861138817237
    lateinit var user: User
    private lateinit var gps: GPS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fire_hot_takes)
        setupTabLayout()
        setupTopNavigationView()

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_Fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        loadUserData()

        checkPermmison()
//        latitude = user?.latitude
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        Log.i("LOCATIONNNNNNNNNNNNNNN", location.toString())
        drawUser(latitude, longitude)
    }


    fun loadUserData() {
        val user = FirebaseAuth.getInstance().currentUser

        //male user's data
        val maleDb = FirebaseDatabase.getInstance().reference.child("male")
        maleDb.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                if (dataSnapshot.key == user?.uid) {
                    getUser(dataSnapshot)
                }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
                if (Profile_Activity.active) {
                    getUser(dataSnapshot)
                }
            }
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        })

        //female user's data
        val femaleDb = FirebaseDatabase.getInstance().reference.child("female")
        femaleDb.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                if (dataSnapshot.key == user?.uid) {
                    getUser(dataSnapshot)
                }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
                if (Profile_Activity.active) {
                    getUser(dataSnapshot)
                }
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }





    private fun getUser(dataSnapshot: DataSnapshot) {
        user = dataSnapshot.getValue(User::class.java)!!
        latitude = user.latitude
        longitude = user.longtitude
        drawUser(latitude, longitude)
        Log.i("USERRRRRR", user.latitude.toString())
        Log.i("USERRRRRR", user.longtitude.toString())
    }



    fun drawUser(latitude: Double, longitude: Double){
        if (mMap != null) {
            val me = LatLng(latitude, longitude)
            mMap!!.addMarker(
                MarkerOptions()
                .position(me)
                .title("You")
                .snippet("You are here")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.charmander)))
            mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(me, 14f))
            BitmapDescriptorFactory.fromFile()
        }

    }



    fun checkPermmison(){
        if(Build.VERSION.SDK_INT>=23){
            if(ActivityCompat.
                checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){

                requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 123)
                return
            }
        }

        GetUserLocation()
    }

    fun GetUserLocation(){
        Toast.makeText(this,"User location access on", Toast.LENGTH_LONG).show()
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

    }


    inner class MylocationListener: LocationListener {
        constructor(){

        }

        override fun onLocationChanged(p0: Location) {
             location=p0
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























    private fun setupTabLayout() {
        tabLayout = findViewById(R.id.tabLayout)

        tabLayout!!.addTab(tabLayout!!.newTab().setText("Hot Takes"))
        tabLayout!!.addTab(tabLayout!!.newTab().setText("Search"))
        tabLayout!!.tabGravity = TabLayout.GRAVITY_FILL

        tabLayout!!.getTabAt(TAB_NUM)?.select()

        tabLayout!!.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab!!.position) {
                    1 -> {
                        val intent = Intent(this@FireHotTakesActivity, FireSearchActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun setupTopNavigationView() {
        Log.d(TAG, "setupTopNavigationView: setting up TopNavigationView")
        val tvEx = findViewById<View>(R.id.topNavViewBar) as BottomNavigationView
        TopNavigationViewHelper.setupTopNavigationView(tvEx)
        TopNavigationViewHelper.enableNavigation(mContext, tvEx)
        val menu = tvEx.menu
        val menuItem = menu.getItem(ACTIVITY_NUM)
        menuItem.isChecked = true
    }

    companion object {
        private const val TAG = "FireHotTakesActivity"
        private const val TAB_NUM = 0
        private const val ACTIVITY_NUM = 1
    }
}