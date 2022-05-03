package com.zellycookies.pineapple.news

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.zellycookies.pineapple.R


private class FetchNewsTask(context: Activity): Thread() {

}


class NewsActivity: AppCompatActivity() {
    private var articles: ArrayList<News> = ArrayList()
    private lateinit var newsListView: ListView
    private lateinit var adapter: NewsListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)
        val context = this
        newsListView = findViewById<ListView>(R.id.newsListView)

        Thread(Runnable {
            articles = fetchNews()
            this@NewsActivity.runOnUiThread(Runnable {
                this.adapter = NewsListAdapter(context, articles)
                this.newsListView.adapter = adapter
            })
        }).start()

    }

}


class NewsListAdapter (
    private val context: Activity,
    private val articles: ArrayList<News>
): ArrayAdapter<News>(context, R.layout.news_item, articles) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val rowView: View = inflater.inflate(R.layout.news_item, null, false)
        val titleTxtView = rowView.findViewById<TextView>(R.id.newsItemTitle)
        val dateTxtView = rowView.findViewById<TextView>(R.id.newsDateView)
        val imgView = rowView.findViewById<ImageView>(R.id.imageView)

        titleTxtView.setText(articles[position].title)
        dateTxtView.setText(articles[position].published_date)
        imgView.setImageURI(Uri.parse(articles[position].media))
        return rowView
    }
}