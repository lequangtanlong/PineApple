package com.zellycookies.pineapple.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.zellycookies.pineapple.R
import com.bumptech.glide.Glide

class ProfileCheckinMain : AppCompatActivity() {
    private var mContext: Context? = null
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_checkin_main)
        mContext = this@ProfileCheckinMain
        val back: ImageButton = findViewById<View>(R.id.back) as ImageButton
        back.setOnClickListener(View.OnClickListener { onBackPressed() })
        val profileName: TextView = findViewById<TextView>(R.id.name_main)
        val profileImage: ImageView = findViewById<ImageView>(R.id.profileImage)
        val profileBio: TextView = findViewById<TextView>(R.id.bio_beforematch)
        val profileInterest: TextView = findViewById<TextView>(R.id.interests_beforematch)
        val profileDistance: TextView = findViewById<TextView>(R.id.distance_main)
        val profileDob: TextView = findViewById<TextView>(R.id.dob_main)
        val intent: Intent = getIntent()
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
        when (profileImageUrl) {
            "defaultFemale" -> Glide.with(mContext as ProfileCheckinMain).load(R.drawable.img_ava_female)
                .into(profileImage)
            "defaultMale" -> Glide.with(mContext as ProfileCheckinMain).load(R.drawable.img_ava_male).into(profileImage)
            else -> Glide.with(mContext as ProfileCheckinMain).load(profileImageUrl).into(profileImage)
        }
    }
}