package com.zellycookies.pineapple.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.zellycookies.pineapple.utils.TopNavigationViewHelper
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.zellycookies.pineapple.R
import com.zellycookies.pineapple.home.HomeSwipeActivity

class BtnLikeActivity : AppCompatActivity()  {
    private val mContext: Context = this@BtnLikeActivity
    private var like: ImageView? = null
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_btn_like)
        setupTopNavigationView()
        like = findViewById<View>(R.id.like) as ImageView?
        val intent: Intent = getIntent()
        val profileUrl: String? = intent.getStringExtra("url")
        when (profileUrl) {
            "defaultFemale" -> like?.let {
                Glide.with(mContext).load(R.drawable.img_ava_female).into(
                    it
                )
            }
            "defaultMale" -> like?.let { Glide.with(mContext).load(R.drawable.img_ava_male).into(it) }
            else -> like?.let { Glide.with(mContext).load(profileUrl).into(it) }
        }
        Thread {
            try {
                Thread.sleep(1000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            val mainIntent = Intent(this@BtnLikeActivity, HomeSwipeActivity::class.java)
            startActivity(mainIntent)
        }.start()
    }

    private fun setupTopNavigationView() {
        Log.d(TAG, "setupTopNavigationView: setting up TopNavigationView")
        val tvEx: BottomNavigationView =
            findViewById<View>(R.id.topNavViewBar) as BottomNavigationView
        TopNavigationViewHelper.setupTopNavigationView(tvEx)
        TopNavigationViewHelper.enableNavigation(mContext, tvEx)
        val menu: Menu = tvEx.getMenu()
        val menuItem = menu.getItem(ACTIVITY_NUM)
        menuItem.isChecked = true
    }

    companion object {
        private const val TAG = "BtnLikeActivity"
        private const val ACTIVITY_NUM = 1
    }
}