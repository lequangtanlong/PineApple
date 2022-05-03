package com.zellycookies.pineapple.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import com.zellycookies.pineapple.R
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.zellycookies.pineapple.home.HomeSwipeActivity
import com.zellycookies.pineapple.utils.User

class ProfileCheckinMain : AppCompatActivity() {
    private var mContext: Context? = null
    private lateinit var image1: ImageView
    private lateinit var image2: ImageView
    private lateinit var image3: ImageView
    private lateinit var image4: ImageView
    private lateinit var userId: String
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_checkin_main)
        mContext = this@ProfileCheckinMain
        val back: ImageButton = findViewById<View>(R.id.back) as ImageButton
        back.setOnClickListener(View.OnClickListener { onBackPressed() })
        val profileName: TextView = findViewById<TextView>(R.id.name_main)
        val profileImage: ImageView = findViewById<ImageView>(R.id.profileImage)
        image1 = findViewById(R.id.image_1)
        image2 = findViewById(R.id.image_2)
        image3 = findViewById(R.id.image_3)
        image4 = findViewById(R.id.image_4)
        val profileBio: TextView = findViewById<TextView>(R.id.bio_beforematch)
        val profileInterest: TextView = findViewById<TextView>(R.id.interests_beforematch)
        val profileDistance: TextView = findViewById<TextView>(R.id.distance_main)
        val profileDob: TextView = findViewById<TextView>(R.id.dob_main)
        val intent: Intent = getIntent()
        userId = intent.getStringExtra("userId").toString()
        val name: String? = intent.getStringExtra("name")
        val dob: String? = intent.getStringExtra("dob")
        val bio: String? = intent.getStringExtra("bio")
        val interest: String? = intent.getStringExtra("interest")
        val distance: Int = intent.getIntExtra("distance", 1)
        val showDoB: Boolean = intent.getBooleanExtra("showDoB", true)
        val showDistance: Boolean = intent.getBooleanExtra("showDistance", true)
        val append = if (distance == 1) "mile away" else "miles away"
        profileDistance.setText("$distance $append")
        profileName.setText(name)

        val dobText = if (!showDoB && !showDistance) ""
        else {
            "${if (showDoB) dob else ""}" +
                    (if (showDoB && showDistance) " | " else "") +
                    if (showDistance) "$distance km" else ""
        }
        profileDob.setText(dobText)
        profileBio.setText(bio)
        profileInterest.setText(interest)
        val profileImageUrl: String? = intent.getStringExtra("photo")
        Log.d("ProfileCheckinMain", "profileImage: $profileImageUrl")
        when (profileImageUrl) {
            "defaultFemale" -> Glide.with(mContext as ProfileCheckinMain).load(R.drawable.img_ava_female)
                .into(profileImage)
            "defaultMale" -> Glide.with(mContext as ProfileCheckinMain).load(R.drawable.img_ava_male).into(profileImage)
            else -> Glide.with(mContext as ProfileCheckinMain).load(profileImageUrl).into(profileImage)
        }
        checkUserSex()
    }

    private fun loadImages(userSex : String, userId : String) {
        Log.d("ProfileCheckinMain", "Loading images...")
        val userRef = FirebaseDatabase.getInstance().reference.child(userSex).child(userId)
        for (i in 0..3) loadSingleImage(userRef, i + 1)
    }

    private fun loadSingleImage(userRef : DatabaseReference, id: Int) {
        userRef.child("imageUrl_$id").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.value != null) {
                    var img : ImageView? = null
                    img = when (id) {
                        1 -> image1
                        2 -> image2
                        3 -> image3
                        4 -> image4
                        else -> null
                    }
                    Log.d("ProfileCheckinMain", "imageUrl_$id: ${snapshot.value}")
                    if (img != null)
                        Glide.with(mContext as ProfileCheckinMain).load(snapshot.value).into(img)
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun checkUserSex() {
        Log.d("ProfileCheckinMain", "checkUserSex for $userId")
        var userSex: String
        val maleDb = FirebaseDatabase.getInstance().reference.child("male")
        maleDb.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                if (dataSnapshot.key == userId) {
                    userSex = "male"
                    Log.d("ProfileCheckinMain", "user is male")
                    loadImages(userSex, userId)
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
                if (dataSnapshot.key == userId) {
                    userSex = "female"
                    Log.d("ProfileCheckinMain", "user is female")
                    loadImages(userSex, userId)
                }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }
}