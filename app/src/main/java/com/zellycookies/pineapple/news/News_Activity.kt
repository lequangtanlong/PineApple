package com.zellycookies.pineapple.news

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
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
//        imgView.setImageURI(Uri.parse(articles[position].media))
        Glide.with(context).load(articles[position].media).into(imgView)
//        DownloadImageFromInternet(imgView).execute(articles[position].media)
        return rowView
    }

    @SuppressLint("StaticFieldLeak")
    @Suppress("DEPRECATION")
    private inner class DownloadImageFromInternet(var imageView: ImageView) : AsyncTask<String, Void, Bitmap?>() {
        init {
        }
        override fun doInBackground(vararg urls: String): Bitmap? {
            val imageURL = urls[0]
            var image: Bitmap? = null
            try {
                val `in` = java.net.URL(imageURL).openStream()
                image = BitmapFactory.decodeStream(`in`)
            }
            catch (e: Exception) {
                Log.e("Error Message", e.message.toString())
                e.printStackTrace()
            }
            return image
        }
        override fun onPostExecute(result: Bitmap?) {
            imageView.setImageBitmap(result)
        }
    }
}


