package com.zellycookies.pineapple.news

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.Headers

class News (
    var title: String,
    var author: String,
    var published_date: String,
    var link: String,
    var summary: String,
    var media: String
){

}
class Response (
    var status: String,
    var articles: ArrayList<News>
)

public interface NewsAPI {
//        @GET("/search?q={value}&from={date}&countries=VN")
//        fun fetch(
//            @Query("value") value: String?,
//            @Query("date") date: String?,
//            @HeaderMap  headers: Map<String, String>
//        ): Call<Response>

    @GET("search?q=covid`&from=25/4/2022&countries=VN")
    @Headers("x-api-key: f_bhMwTqP8XH_b8pGDNGyVfDxt7O9asGvD3wrWwr3E4")
    fun fetch(): Call<Response>
}

fun fetchNews(): ArrayList<News> {
    var BASE_API = "https://api.newscatcherapi.com/v2/"
    var headers = mapOf(
        "x-api-key" to "f_bhMwTqP8XH_b8pGDNGyVfDxt7O9asGvD3wrWwr3E4"
    )


    val newsAPI: NewsAPI = Retrofit.Builder()
        .baseUrl(BASE_API)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(NewsAPI::class.java)


    //        val call: Call<Response> = newsAPI . fetch ("covid", "25/4/2022", headers)
    val call: Call<Response> = newsAPI.fetch()
    val response: Response? =  call.execute().body()
    var articles = ArrayList<News>()
    if (response?.articles != null)
        articles = response.articles

    for (a in articles)
        println(a.media)
    return articles
}



//fun main() {
//    fetchNews()
//}