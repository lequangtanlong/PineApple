package com.zellycookies.pineapple.utility

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import com.zellycookies.pineapple.R
import com.zellycookies.pineapple.news.NewsActivity
import com.zellycookies.pineapple.utils.TopNavigationViewHelper

class UtilityTopPicksActivity : AppCompatActivity() {

    private var tabLayout : TabLayout? = null

    private val mContext: Context = this@UtilityTopPicksActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_utility_top_picks)
        setupTabLayout()
        setupTopNavigationView()
    }

    private fun setupTabLayout() {
        tabLayout = findViewById(R.id.tabLayout)

        tabLayout!!.addTab(tabLayout!!.newTab().setText("Likes"))
        tabLayout!!.addTab(tabLayout!!.newTab().setText("Top Picks"))
        tabLayout!!.addTab(tabLayout!!.newTab().setText("History"))
        tabLayout!!.addTab(tabLayout!!.newTab().setText("News"))
        tabLayout!!.tabGravity = TabLayout.GRAVITY_FILL

        tabLayout!!.getTabAt(TAB_NUM)?.select()

        tabLayout!!.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab!!.position) {
                    0 -> {
                        val intent = Intent(this@UtilityTopPicksActivity, UtilityLikesActivity::class.java)
                        startActivity(intent)
                    }
                    2 -> {
                        val intent = Intent(mContext, UtilityHistoryActivity::class.java)
                        startActivity(intent)
                    }
                    3 -> {
                        val intent = Intent(mContext, NewsActivity::class.java)
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
        private const val TAG = "UtilityTopPicksActivity"
        private const val TAB_NUM = 1
        private const val ACTIVITY_NUM = 3
    }
}