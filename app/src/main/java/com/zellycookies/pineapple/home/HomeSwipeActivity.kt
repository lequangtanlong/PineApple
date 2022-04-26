package com.zellycookies.pineapple.home

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.lorentzos.flingswipe.SwipeFlingAdapterView
import com.zellycookies.pineapple.R
import com.zellycookies.pineapple.introduction.IntroductionMain
import com.zellycookies.pineapple.main.*
import com.zellycookies.pineapple.utils.CalculateAge
import com.zellycookies.pineapple.utils.GPS
import com.zellycookies.pineapple.utils.TopNavigationViewHelper
import com.zellycookies.pineapple.utils.User


class HomeSwipeActivity : Activity() {
    private val MY_PERMISSIONS_REQUEST_LOCATION = 123
    private val mContext: Context = this@HomeSwipeActivity
    private var userSex: String? = null
    private var lookforSex: String? = null
    private var latitude = 37.349642
    private var longtitude = -121.938987
    private var currentUID: String? = null
    private var SE = false
    private var oop = false
    private var ui = false
    private var db = false
    private val name: String? = null
    private val bio: String? = null
    private val interest: String? = null
    private var mNotificationHelper: NotificationHelper? = null
    private val cards_data: Array<Cards> = arrayOf()
    private var arrayAdapter: PhotoAdapter? = null
    var listView: ListView? = null
    var rowItems: MutableList<Cards>? = null
    var swipedCards = ArrayDeque<Cards>(listOf())
    var gps: GPS? = null

    private var tabLayout : TabLayout? = null

    //firebase
    private var mAuth: FirebaseAuth? = null
    private var mAuthListener: FirebaseAuth.AuthStateListener? = null
    private var usersDb: DatabaseReference? = null
    private var mFirebaseFirestore: FirebaseFirestore? = null

    private lateinit var flingContainer: SwipeFlingAdapterView

    private lateinit var rewindButton: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_swipe)
        createNotificationChannel()
        usersDb = FirebaseDatabase.getInstance().reference
        mFirebaseFirestore = FirebaseFirestore.getInstance()
        mNotificationHelper = NotificationHelper(this)
        setupFirebaseAuth()
        setupTabLayout()
        setupTopNavigationView()
        checkUserSex()
        rowItems = ArrayList()
        arrayAdapter = PhotoAdapter(this, R.layout.item, rowItems as ArrayList<Cards>)
        updateSwipeCard()
    }

    private fun updateLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                MY_PERMISSIONS_REQUEST_LOCATION
            )
        } else {
            gps = GPS(this)
            val location = gps!!.mlocation
            // made the changes to set location only if location object is not null, else default location is taken
            if (location != null) {
                latitude = location.latitude
                longtitude = location.longitude
            }
            val curDB = FirebaseDatabase.getInstance().reference.child(userSex!!).child(
                currentUID!!
            )
            val userLoc: MutableMap<Any, Any> = mutableMapOf()
            userLoc["latitude"] = latitude
            userLoc["longtitude"] = longtitude
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                run {
                    if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        if (ContextCompat.checkSelfPermission(
                                this,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            )
                            == PackageManager.PERMISSION_GRANTED
                        ) {
                            updateLocation()
                        } else {
                            Toast.makeText(
                                this@HomeSwipeActivity,
                                "Location Permission Denied. You have to give permission inorder to know the user range ",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }
                }
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun updateSwipeCard() {
        flingContainer = findViewById<View>(R.id.frame) as SwipeFlingAdapterView
        flingContainer.adapter = arrayAdapter

        rewindButton = findViewById(R.id.rewindbtn)
        rewindButton.setOnClickListener {
            if (swipedCards.size != 0) {
                val swipedCard = swipedCards.removeFirst()
                rowItems!!.add(1, swipedCard)
                arrayAdapter?.notifyDataSetChanged()
            }
        }

        flingContainer.setFlingListener(object : SwipeFlingAdapterView.onFlingListener {
            override fun removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!")

                val removedCard = rowItems!!.removeAt(0)
                swipedCards.addFirst(removedCard)
                arrayAdapter?.notifyDataSetChanged()
            }

            override fun onLeftCardExit(dataObject: Any) {
                val obj = dataObject as Cards
                val userId = obj.userId
                usersDb!!.child(lookforSex!!).child(userId).child("connections").child("dislikeme")
                    .child(
                        currentUID!!
                    ).setValue(true)
            }

            override fun onRightCardExit(dataObject: Any) {
                val obj = dataObject as Cards
                val userId = obj.userId
                usersDb!!.child(lookforSex!!).child(userId).child("connections").child("likeme")
                    .child(
                        currentUID!!
                    ).setValue(true)

                //check matches
                isConnectionMatch(userId)
            }

            override fun onAdapterAboutToEmpty(itemsInAdapter: Int) {
                // Ask for more data here
            }

            override fun onScroll(scrollProgressPercent: Float) {
                val view = flingContainer.selectedView
                view.findViewById<View>(R.id.item_swipe_right_indicator).alpha = (if (scrollProgressPercent < 0) (-scrollProgressPercent) else 0.0F)
                view.findViewById<View>(R.id.item_swipe_left_indicator).alpha = (if (scrollProgressPercent > 0) scrollProgressPercent else 0.0F)
            }
        })
    }

/*    fun RewindBtn(v: View?) {
        if (swipedCards.size != 0) {
            val swipedCard = swipedCards.removeFirst()
            rowItems!!.add(0, swipedCard)
            arrayAdapter?.notifyDataSetChanged()
        }
    }*/

    private fun isConnectionMatch(userId: String) {
        val currentUserConnectionsDb = usersDb!!.child(userSex!!).child(currentUID!!).child("connections").child("likeme").child(userId)
        currentUserConnectionsDb.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {

                    //prompt user that match
                    //later change to notification
                    sendNotification()
                    usersDb!!.child(lookforSex!!).child(dataSnapshot.key!!).child("connections")
                        .child("match_result").child(
                            currentUID!!
                        ).setValue(true)
                    usersDb!!.child(userSex!!).child(currentUID!!).child("connections")
                        .child("match_result").child(
                            dataSnapshot.key!!
                        ).setValue(true)

                    // Create group in Realtime DB and Cloud DB
                    val key = mFirebaseFirestore!!.collection("group").document().id
                    val groupInfoDb = mFirebaseFirestore!!.collection("group").document(key)
                    val idUserList = ArrayList<String?>()
                    idUserList.add(dataSnapshot.key)
                    idUserList.add(currentUID)
                    val newGroupMap = HashMap<String, String>()
                    newGroupMap["idGroup"] = key
                    groupInfoDb.set(newGroupMap)
                    groupInfoDb.update("members", idUserList)
                    groupInfoDb.update("createTime", FieldValue.serverTimestamp())
                    usersDb!!.child(lookforSex!!).child(dataSnapshot.key!!).child("group").child(
                        currentUID!!
                    ).setValue(key)
                    usersDb!!.child(userSex!!).child(currentUID!!).child("group")
                        .child(dataSnapshot.key!!).setValue(key)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    fun sendNotification() {
        /*val nb = mNotificationHelper!!.getChannel1Notification(
            mContext.getString(R.string.app_name),
            mContext.getString(R.string.match_notification)
        )
        mNotificationHelper!!.manager!!.notify(1, nb.build())*/

        createNotificationChannel()

        val builder = NotificationCompat.Builder(this, "channel_match")
            .setSmallIcon(R.drawable.ic_baseline_notifications_24)
            .setContentTitle("PineApple")
            .setContentText("You've got a new match!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(this)) {
            notify(0, builder.build())
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Matching Channel"
            val descriptionContent = "A channel for matching"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("channel_match", name, importance).apply {
                description = descriptionContent
            }
            val notificationManager : NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


    override fun onBackPressed() {
    }

    /**
     * check the user sex
     */
    fun checkUserSex() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            currentUID = user.uid
            val maleDb = FirebaseDatabase.getInstance().reference.child("male")
            maleDb.addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                    if (dataSnapshot.key == currentUID) {
                        Log.d(
                            TAG,
                            "onChildAdded: the sex is $userSex"
                        )
                        userSex = "male"
                        //updateLocation
                        updateLocation()
                        lookforSex = dataSnapshot.getValue(
                            User::class.java
                        )!!.preferSex
                        findInterest(dataSnapshot)
                        potentialMatch
                    }
                }

                override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
                override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
                override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
                override fun onCancelled(databaseError: DatabaseError) {}
            })
            val femaleDb = FirebaseDatabase.getInstance().reference.child("female")
            femaleDb.addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                    if (dataSnapshot.key == currentUID) {
                        Log.d(
                            TAG,
                            "onChildAdded: the sex is $userSex"
                        )
                        userSex = "female"
                        //update location
                        updateLocation()
                        lookforSex = dataSnapshot.getValue(
                            User::class.java
                        )!!.preferSex
                        findInterest(dataSnapshot)
                        potentialMatch
                    }
                }

                override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
                override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
                override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }
    }

    private fun findInterest(dataSnapshot: DataSnapshot) {
        SE = dataSnapshot.getValue(User::class.java)!!.isSE
        oop = dataSnapshot.getValue(User::class.java)!!.isOop
        db = dataSnapshot.getValue(User::class.java)!!.isDatabase
        ui = dataSnapshot.getValue(User::class.java)!!.isDesign
    }//calculate age

    //initialize card view
    //check profile image first

    //calculate distance
    /**
     * show the lookforsex profile photos
     */
    val potentialMatch: Unit
        get() {
            val potentialMatch = FirebaseDatabase.getInstance().reference.child(
                lookforSex!!
            )
            potentialMatch.addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                    if (dataSnapshot.exists() && !dataSnapshot.child("connections")
                            .child("dislikeme").hasChild(
                                currentUID!!
                            ) && !dataSnapshot.child("connections").child("likeme")
                            .hasChild(currentUID!!) && dataSnapshot.key != currentUID
                    ) {
                        val curUser = dataSnapshot.getValue(
                            User::class.java
                        )
                        val tempDatabase = curUser!!.isDatabase
                        val tempOop = curUser.isOop
                        val tempDesign = curUser.isDesign
                        val tempSE = curUser.isSE
                        val showDoB = curUser.isShowDoB
                        val showDistance = curUser.isShowDistance
                        if (tempOop == oop || tempDesign == ui || tempDatabase == db || tempSE == SE) {
                            //calculate age
                            val dob = curUser.dateOfBirth
                            val cal = CalculateAge(dob!!)
                            val age = cal.age

                            //initialize card view
                            //check profile image first
                            var profileImageUrl =
                                if (lookforSex == "female") "defaultFemale" else "defaultMale"
                            if (dataSnapshot.child("profileImageUrl").value != null) {
                                profileImageUrl =
                                    dataSnapshot.child("profileImageUrl").value.toString()
                            }
                            val username = curUser.username
                            val bio = curUser.description
                            val interest = StringBuilder()
                            if (tempSE) {
                                interest.append("SE   ")
                            }
                            if (tempOop) {
                                interest.append("OOP   ")
                            }
                            if (tempDesign) {
                                interest.append("UI Design   ")
                            }
                            if (tempDatabase) {
                                interest.append("Database   ")
                            }

                            //calculate distance
                            // gps = GPS(mContext)
                            Log.d(
                                TAG,
                                "onChildAdded: the x, y of user is $latitude, $longtitude"
                            )
                            Log.d(
                                TAG,
                                "onChildAdded: the other user x y is " + curUser.latitude + ", " + curUser.longtitude
                            )
                            val distance =
                                if (gps != null)
                                    gps!!.calculateDistance(
                                        latitude,
                                        longtitude,
                                        curUser.latitude,
                                        curUser.longtitude
                                    )
                                else 0
                            Log.d(
                                TAG,
                                "distance is " + distance
                            )
                            val item = Cards(
                                dataSnapshot.key!!, username, dob, age,
                                profileImageUrl, bio, interest.toString(), distance, showDoB, showDistance
                            )
                            rowItems!!.add(item)
                            arrayAdapter?.notifyDataSetChanged()
                        }
                    }
                }

                override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
                override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
                override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }

    //Dislike Button = Swipe left
    fun DislikeBtn(v: View?) {
        if (rowItems!!.size != 0) {
            val card_item = rowItems!![0]
            val userId = card_item.userId
            usersDb!!.child(lookforSex!!).child(userId).child("connections").child("dislikeme")
                .child(
                    currentUID!!
                ).setValue(true)
            rowItems!!.removeAt(0)
            arrayAdapter?.notifyDataSetChanged()
            val btnClick = Intent(mContext, BtnDislikeActivity::class.java)
            btnClick.putExtra("url", card_item.profileImageUrl)
            startActivity(btnClick)
        }
    }

    //Like Button = Swipe right
    fun LikeBtn(v: View?) {
        if (rowItems!!.size != 0) {
            val card_item = rowItems!![0]
            val userId = card_item.userId
            usersDb!!.child(lookforSex!!).child(userId).child("connections").child("likeme").child(
                currentUID!!
            ).setValue(true)

            //check matches
            isConnectionMatch(userId)

            rowItems!!.removeAt(0)

            arrayAdapter?.notifyDataSetChanged()
            val btnClick = Intent(mContext, BtnLikeActivity::class.java)
            btnClick.putExtra("url", card_item.profileImageUrl)
            startActivity(btnClick)
        }
    }



    private fun setupTabLayout() {
        tabLayout = findViewById(R.id.tabLayout)

        tabLayout!!.addTab(tabLayout!!.newTab().setText("Swipe"))
        tabLayout!!.addTab(tabLayout!!.newTab().setText("Blind Date"))
        tabLayout!!.tabGravity = TabLayout.GRAVITY_FILL

        tabLayout!!.getTabAt(TAB_NUM)?.select()

        tabLayout!!.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    /**
     * setup top tool bar
     */
    private fun setupTopNavigationView() {
        Log.d(TAG, "setupTopNavigationView: setting up TopNavigationView")
        val tvEx = findViewById<View>(R.id.topNavViewBar) as BottomNavigationView
        TopNavigationViewHelper.setupTopNavigationView(tvEx)
        TopNavigationViewHelper.enableNavigation(mContext, tvEx)
        val menu = tvEx.menu
        val menuItem = menu.getItem(ACTIVITY_NUM)
        menuItem.isChecked = true
    }

    /**
     * check to see if the @param 'user' is logged in
     * @param user
     */
    private fun checkCurrentUser(user: FirebaseUser?) {
        Log.d(TAG, "checkCurrentUser: checking if user is logged in")
        if (user == null) {
            val intent = Intent(mContext, IntroductionMain::class.java)
            startActivity(intent)
        }
    }

    /**
     * Setup the firebase auth object
     */
    private fun setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: check user")
        mAuth = FirebaseAuth.getInstance()
        mAuthListener = AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser

            //check if the user is logged in
            checkCurrentUser(user)
            if (user != null) {
                // user is signed in
                Log.d(TAG, "onAuthStateChanged: signed_in:" + user.uid)
            } else {
                //user is signed out
                Log.d(TAG, "onAuthStateChanged: signed_out")
            }
        }
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        mAuth!!.addAuthStateListener(mAuthListener!!)
        checkCurrentUser(mAuth!!.currentUser)
    }

    public override fun onStop() {
        super.onStop()
        if (mAuthListener != null) {
            mAuth!!.removeAuthStateListener(mAuthListener!!)
        }
    }

    companion object {
        private const val TAG = "HomeSwipeActivity"
        private const val TAB_NUM = 0
        private const val ACTIVITY_NUM = 0
    }
}