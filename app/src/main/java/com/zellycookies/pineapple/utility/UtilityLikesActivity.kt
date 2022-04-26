package com.zellycookies.pineapple.utility

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import com.zellycookies.pineapple.R
import com.zellycookies.pineapple.utils.TopNavigationViewHelper
import com.zellycookies.pineapple.profile.ViewWhoLikesYouActivity

class UtilityLikesActivity : AppCompatActivity() {

    private var tabLayout : TabLayout? = null

    private val mContext: Context = this@UtilityLikesActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_utility_likes)
        setupTabLayout()
        setupTopNavigationView()

        val btnViewWhoLikesYou = findViewById<View>(R.id.btnVWLY) as Button
        btnViewWhoLikesYou.setOnClickListener {
            val intent = Intent(this@UtilityLikesActivity, ViewWhoLikesYouActivity::class.java)
            startActivity(intent)
        }

        val btnViewWhoYouLike = findViewById<View>(R.id.btnVWYL) as Button
        btnViewWhoYouLike.setOnClickListener {
            Log.d("View Who You Like", "Hello world")
            val intent1 = Intent(this@UtilityLikesActivity, ViewWhoYouLikeActivity::class.java)
            startActivity(intent1)
        }
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
                    1 -> {
                        val intent = Intent(this@UtilityLikesActivity, UtilityTopPicksActivity::class.java)
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
        private const val TAG = "UtilityLikesActivity"
        private const val TAB_NUM = 0
        private const val ACTIVITY_NUM = 3
    }
}