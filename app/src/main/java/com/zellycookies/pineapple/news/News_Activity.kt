package com.zellycookies.pineapple.news

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.zellycookies.pineapple.R




class NewsActivity: AppCompatActivity() {
    private var articles: ArrayList<News>? = fetchNews()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        var newsRecyclerView = findViewById<RecyclerView>(R.id.newsRecyclerView)
//        var adapter = NewsListAdapter(this, articles)
    }

}


