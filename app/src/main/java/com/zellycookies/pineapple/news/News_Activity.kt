package com.zellycookies.pineapple.news

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.zellycookies.pineapple.R
import com.zellycookies.pineapple.home.HomeSwipeActivity
import com.zellycookies.pineapple.utility.UtilityLikesActivity


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
        val back = findViewById<ImageButton>(R.id.back)
        back.setOnClickListener { startActivity( Intent(this, UtilityLikesActivity::class.java)) }
        Thread(Runnable {
            articles = fetchNews()
            this@NewsActivity.runOnUiThread(Runnable {
                this.adapter = NewsListAdapter(context, articles)
                this.newsListView.adapter = adapter
                this.newsListView.setOnItemClickListener {adapterView, view, i, l ->
                    val intent = Intent(context, NewsDetailActivity::class.java)
                    intent.putExtra("link", articles[i].link)
                    startForResult.launch(intent)
                }
            })
        }).start()

    }
    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->

    }

    override fun onBackPressed() {
        val intent = Intent(this, UtilityLikesActivity::class.java)
        startActivity(intent)
        finish()
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


