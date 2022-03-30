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
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx

class TopNavigationViewHelper {
    private val TAG = "TopNavigationViewHelper"

    companion object {
        fun setupTopNavigationView(tv: BottomNavigationView) {
            Log.d(ContentValues.TAG, "setupTopNavigationView: setting up navigationview")
        }

        fun enableNavigation(context: Context, view: BottomNavigationView) {
            view.setOnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.ic_profile -> {
                        val intent2 = Intent(context, Profile_Activity::class.java)
                        context.startActivity(intent2)
                    }
                    R.id.ic_main -> {
                        val intent1 = Intent(context, MainActivity::class.java)
                        context.startActivity(intent1)
                    }
                    R.id.ic_matched -> {
                        val intent3 = Intent(context, Matched_Activity::class.java)
                        context.startActivity(intent3)
                    }
                }
                false
            }
        }
    }
}