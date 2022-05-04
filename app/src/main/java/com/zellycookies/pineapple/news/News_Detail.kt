package com.zellycookies.pineapple.news

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.zellycookies.pineapple.R
import com.zellycookies.pineapple.utility.UtilityLikesActivity
import com.zellycookies.pineapple.utility.UtilityNewsActivity


class NewsDetailActivity: AppCompatActivity() {
    lateinit var webView: WebView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_detail)
        val context = this
        val webView = findViewById<WebView>(R.id.webView)
        val url = intent.getStringExtra("link")
        webView.webViewClient = WebViewClient()
        if (url != null) {
            webView.loadUrl(url)
        }
        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true

        val back = findViewById<ImageButton>(R.id.back)
        back.setOnClickListener { onBackPressed() }
    }

    override fun onBackPressed() {
        //if (webView.canGoBack()) {
        //    webView.goBack()
        //} else {
            //super.onBackPressed()

            val intent = Intent(this, NewsActivity::class.java)
            startActivity(intent)
            finish()
        //}
    }


}

