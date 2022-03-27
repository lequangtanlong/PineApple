package com.ZellyCookies.PineApple.Utils

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.util.Log
import com.ZellyCookies.PineApple.Main.MainActivity
import com.ZellyCookies.PineApple.Matched.Matched_Activity
import com.ZellyCookies.PineApple.Profile.Profile_Activity
import com.ZellyCookies.PineApple.R
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx

class TopNavigationViewHelper {
    private val TAG = "TopNavigationViewHelper"

    companion object {
        fun setupTopNavigationView(tv: BottomNavigationViewEx) {
            Log.d(ContentValues.TAG, "setupTopNavigationView: setting up navigationview")
            tv.enableAnimation(false)
            tv.enableItemShiftingMode(false)
            tv.enableShiftingMode(false)
            tv.setTextVisibility(false)
            tv.setIconSize(30f, 30f)
        }

        fun enableNavigation(context: Context, view: BottomNavigationViewEx) {
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