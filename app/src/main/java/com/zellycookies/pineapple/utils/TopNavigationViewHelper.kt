package com.zellycookies.pineapple.utils

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.zellycookies.pineapple.main.MainActivity
import com.zellycookies.pineapple.matched.Matched_Activity
import com.zellycookies.pineapple.profile.Profile_Activity
import com.zellycookies.pineapple.R
import com.zellycookies.pineapple.fire.FireHotTakesActivity
import com.zellycookies.pineapple.utility.UtilityLikesActivity

class TopNavigationViewHelper {
    private val TAG = "TopNavigationViewHelper"

    companion object {
        fun setupTopNavigationView(tv: BottomNavigationView) {
            Log.d(ContentValues.TAG, "setupTopNavigationView: setting up navigationview")
        }

        fun enableNavigation(context: Context, view: BottomNavigationView) {
            view.setOnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.ic_main -> {
                        val intent = Intent(context, MainActivity::class.java)
                        context.startActivity(intent)
                    }
                    R.id.ic_fire -> {
                        val intent = Intent(context, FireHotTakesActivity::class.java)
                        context.startActivity(intent)
                    }
                    R.id.ic_matched -> {
                        val intent = Intent(context, Matched_Activity::class.java)
                        context.startActivity(intent)
                    }
                    R.id.ic_utility -> {
                        val intent = Intent(context, UtilityLikesActivity::class.java)
                        context.startActivity(intent)
                    }
                    R.id.ic_settings -> {
                        val intent = Intent(context, Profile_Activity::class.java)
                        context.startActivity(intent)
                    }
                }
                false
            }
        }
    }
}