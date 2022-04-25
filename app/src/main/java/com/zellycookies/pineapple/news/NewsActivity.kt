package com.zellycookies.pineapple.News

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.zellycookies.pineapple.R
import com.zellycookies.pineapple.databinding.ActivityNewsBinding


class NewsActivity : AppCompatActivity() {
    private var api = "https://api.newscatcherapi.com/v2/search?q=covid&from=23/4/2022&countries=VN"
    private var headers = HashMap<String, String>()
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityNewsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_news)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        headers["x-api-key"] = "f_bhMwTqP8XH_b8pGDNGyVfDxt7O9asGvD3wrWwr3E4"
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_news)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}