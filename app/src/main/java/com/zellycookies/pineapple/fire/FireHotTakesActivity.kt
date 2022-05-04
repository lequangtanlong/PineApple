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
import androidx.core.content.ContextCompat
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

class anonymous(latitude: Double, longitude: Double, avatar: Int)

class FireHotTakesActivity : AppCompatActivity(), OnMapReadyCallback {

    private var tabLayout : TabLayout? = null

    private val mContext: Context = this@FireHotTakesActivity

    private var mMap: GoogleMap? = null
    var location: Location?=null
    var latitude=10.795524512999489
    var longitude=106.7231634875081
    lateinit var user: User
    private lateinit var gps: GPS
    var anonymousAvatars= arrayListOf(
        R.drawable.a1,
        R.drawable.a2,
        R.drawable.a3,
        R.drawable.a4,
    )
    var anonymousUsers = arrayListOf<anonymous>()

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
        loadUserData()
        Log.i("LOCATIONNNNNNNNNNNNNNN", location.toString())
        drawUser(latitude, longitude, R.drawable.me)
        loadAnonymousUsers

    }


    fun loadUserData() {
        val user = FirebaseAuth.getInstance().currentUser

        //male user's data
        val maleDb = FirebaseDatabase.getInstance().reference.child("male")
        maleDb.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                if (dataSnapshot.key == user?.uid) {
                    loadUser(dataSnapshot)
                }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
                if (Profile_Activity.active) {
                    loadUser(dataSnapshot)
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
                    loadUser(dataSnapshot)
                }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
                if (Profile_Activity.active) {
                    loadUser(dataSnapshot)
                }
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }





    private fun loadUser(dataSnapshot: DataSnapshot) {
        user = dataSnapshot.getValue(User::class.java)!!
        latitude = user.latitude
        longitude = user.longtitude
//        drawUser(latitude, longitude, R.drawable.me)
        drawUser(10.7933948075473, 106.72337195556752, R.drawable.a1)
        Log.i("USERRRRRR", user.latitude.toString())
        Log.i("USERRRRRR", user.longtitude.toString())
    }



    fun drawUser(latitude: Double, longitude: Double, avatar: Int){
        if (mMap != null) {
            Log.i("ANONYMOUSSSSSS", latitude.toString())
            Log.i("ANONYMOUSSSSSS", longitude.toString())
            val me = LatLng(latitude, longitude)
            mMap!!.addMarker(
                MarkerOptions()
                .position(me)
                .title("You")
                .snippet("You are here")
                .icon(BitmapDescriptorFactory.fromResource(avatar,)))
            if (avatar == R.drawable.me)
                mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(me, 14f))
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






    val loadAnonymousUsers: Unit
        get() {
            val potentialMatch = FirebaseDatabase.getInstance().reference.child("female")
            potentialMatch.addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                    val curUser = dataSnapshot.getValue(
                        User::class.java
                    )
                    val lat = curUser?.latitude
                    val long = curUser?.longtitude


                    if (lat != null && long != null){
                        val i = anonymousAvatars.random()
                        anonymousUsers.add(anonymous(lat, long, i))

                        drawUser(lat, long, i)

                    }
//                        val tempFood = curUser!!.isHobby_food
//                        val tempMusic = curUser.isHobby_music
//                        val tempArt = curUser.isHobby_art
//                        val tempMovies = curUser.isHobby_movies
//                        val showDoB = curUser.isShowDoB
//                        val showDistance = curUser.isShowDistance
//                        if (tempMusic == music || tempArt == art || tempFood == food || tempMovies == movies) {
//                            //calculate age
//                            val dob = curUser.dateOfBirth
//                            val cal = CalculateAge(dob!!)
//                            val age = cal.age
//
//                            //initialize card view
//                            //check profile image first
//                            var profileImageUrl =
//                                if (lookforSex == "female") "defaultFemale" else "defaultMale"
//                            if (dataSnapshot.child("profileImageUrl").value != null) {
//                                profileImageUrl =
//                                    dataSnapshot.child("profileImageUrl").value.toString()
//                            }
//                            val username = curUser.username
//                            val bio = curUser.description
//                            val interest = StringBuilder()
//                            if (tempMovies) {
//                                interest.append("Movies   ")
//                            }
//                            if (tempMusic) {
//                                interest.append("Music   ")
//                            }
//                            if (tempArt) {
//                                interest.append("Art   ")
//                            }
//                            if (tempFood) {
//                                interest.append("Food   ")
//                            }
//
//                            //calculate distance
//                            // gps = GPS(mContext)
//                            Log.d(
//                                HomeSwipeActivity.TAG,
//                                "onChildAdded: the x, y of user is $latitude, $longtitude"
//                            )
//                            Log.d(
//                                HomeSwipeActivity.TAG,
//                                "onChildAdded: the other user x y is " + curUser.latitude + ", " + curUser.longtitude
//                            )
//                            val distance =
//                                if (gps != null)
//                                    gps!!.calculateDistance(
//                                        latitude,
//                                        longtitude,
//                                        curUser.latitude,
//                                        curUser.longtitude
//                                    )
//                                else 0
//                            Log.d(
//                                HomeSwipeActivity.TAG,
//                                "distance is " + distance
//                            )

//                            if (age in minAge..maxAge && distance <= distancePreference) {
//                                val item = Cards(
//                                    dataSnapshot.key!!,
//                                    username,
//                                    dob,
//                                    age,
//                                    profileImageUrl,
//                                    bio,
//                                    interest.toString(),
//                                    distance,
//                                    showDoB,
//                                    showDistance
//                                )
//                                rowItems!!.add(item)
//                                arrayAdapter?.notifyDataSetChanged()
//                            }
//                            val item = Cards(
//                                dataSnapshot.key!!,
//                                username,
//                                dob,
//                                age,
//                                profileImageUrl,
//                                bio,
//                                interest.toString(),
//                                distance,
//                                showDoB,
//                                showDistance
//                            )
//                            rowItems!!.add(item)
//                            arrayAdapter?.notifyDataSetChanged()
                }


                override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
                override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
                override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
                override fun onCancelled(databaseError: DatabaseError) {}
            })
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