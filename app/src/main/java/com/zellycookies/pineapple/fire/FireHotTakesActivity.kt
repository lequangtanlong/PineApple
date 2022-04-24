package com.zellycookies.pineapple.fire

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import com.zellycookies.pineapple.R
import com.zellycookies.pineapple.utility.UtilityTopPicksActivity
import com.zellycookies.pineapple.utils.TopNavigationViewHelper

class FireHotTakesActivity : AppCompatActivity() {

    private var tabLayout : TabLayout? = null

    private val mContext: Context = this@FireHotTakesActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fire_hot_takes)
        setupTabLayout()
        setupTopNavigationView()
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