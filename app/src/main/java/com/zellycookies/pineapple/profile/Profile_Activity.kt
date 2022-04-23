package com.zellycookies.pineapple.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.zellycookies.pineapple.login.Login
import com.zellycookies.pineapple.utils.TopNavigationViewHelper
import com.zellycookies.pineapple.utils.User
import com.bumptech.glide.Glide
import com.bumptech.glide.util.Util
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.database.*
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx
import com.zellycookies.pineapple.R

class Profile_Activity : AppCompatActivity() {
    //initialize display data
    private val mContext: Context = this@Profile_Activity
    private var imagePerson: ImageView? = null
    private var name: TextView? = null
    private var DoBAndSex: TextView? = null
    private var userId: String? = null

    //setup firebase
    private var mAuth: FirebaseAuth? = null
    private var mAuthListener: AuthStateListener? = null
    private val mPhotoDB: DatabaseReference? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate: create the page")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        //setup firebase method
        setupFirebaseAuth()
        setupTopNavigationView()

        //setup display content
        userId = FirebaseAuth.getInstance().currentUser!!.uid
        imagePerson = findViewById(R.id.circle_profile_image)
        name = findViewById(R.id.profile_name)
        DoBAndSex = findViewById(R.id.profile_dob)

        //get user's data
        findUser()

        //setup buttons
        val edit_btn = findViewById<View>(R.id.edit_profile) as ImageButton
        edit_btn.setOnClickListener {
            val intent = Intent(this@Profile_Activity, EditProfileActivity::class.java)
            startActivity(intent)
        }
        val settings = findViewById<View>(R.id.settings) as ImageButton
        settings.setOnClickListener {
            val intent = Intent(this@Profile_Activity, SettingsActivity::class.java)
            startActivity(intent)
        }
        val btnVWLY = findViewById<View>(R.id.btnVWLY)
        btnVWLY.setOnClickListener {
            val intent = Intent(this@Profile_Activity, ViewWhoLikesYouActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: resume to the page")
        findUser()
    }

    fun findUser() {
        val user = FirebaseAuth.getInstance().currentUser

        //male user's data
        val maleDb = FirebaseDatabase.getInstance().reference.child("male")
        maleDb.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                if (dataSnapshot.key == userId) {
                    Log.d(TAG, "onChildAdded: the sex is male")
                    getUserPhotoAndName(dataSnapshot)
                }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
                if (active) {
                    getUserPhotoAndName(dataSnapshot)
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
                if (dataSnapshot.key == user!!.uid) {
                    Log.d(TAG, "onChildAdded: the sex is female")
                    getUserPhotoAndName(dataSnapshot)
                }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
                if (active) {
                    getUserPhotoAndName(dataSnapshot)
                }
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    //get user's info to display
    private fun getUserPhotoAndName(dataSnapshot: DataSnapshot) {
        name!!.text = dataSnapshot.getValue(User::class.java)!!.username
        DoBAndSex!!.text = "Born: " + dataSnapshot.getValue(
            User::class.java
        )!!.dateOfBirth + " | Gender: " + dataSnapshot.getValue(
            User::class.java
        )!!.sex
        val profileImageUrl = dataSnapshot.getValue(
            User::class.java
        )!!.profileImageUrl

        //set avatar
        if (Util.isOnMainThread()) {
            when (profileImageUrl) {
                "defaultFemale" -> imagePerson?.let {
                    Glide.with(applicationContext).load(R.drawable.img_ava_female)
                        .into(it)
                }
                "defaultMale" -> imagePerson?.let {
                    Glide.with(applicationContext).load(R.drawable.img_ava_male)
                        .into(it)
                }
                else -> Glide.with(applicationContext).load(profileImageUrl).into(imagePerson!!)
            }
        }
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
    //----------------------------------------Firebase----------------------------------------
    /**
     * Setup the firebase auth object
     */
    private fun setupFirebaseAuth() {
        mAuth = FirebaseAuth.getInstance()
        mAuthListener = AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                // user is signed in
                Log.d(TAG, "onAuthStateChanged: signed_in:" + user.uid)
            } else {
                //user is signed out
                Log.d(TAG, "onAuthStateChanged: signed_out")
                Log.d(TAG, "onAuthStateChanged: navigating back to login screen.")
                val intent = Intent(this@Profile_Activity, Login::class.java)

                //clear the activity stack， in case when sign out, the back button will bring the user back to the previous activity
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }
    }

    public override fun onStart() {
        super.onStart()
        active = true
        Log.d(TAG, "onStart: " + active)
        // Check if user is signed in (non-null) and update UI accordingly.
        mAuth!!.addAuthStateListener(mAuthListener!!)
    }

    public override fun onStop() {
        super.onStop()
        active = false
        Log.d(TAG, "onStop: " + active)
        if (mAuthListener != null) {
            mAuth!!.removeAuthStateListener(mAuthListener!!)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        active = false
        Log.d(TAG, "onDestroy: " + active)
        if (mAuthListener != null) {
            mAuth!!.removeAuthStateListener(mAuthListener!!)
        }
    }

    companion object {
        private const val TAG = "Profile_Activity"
        private const val ACTIVITY_NUM = 3
        var active = false
    }
}