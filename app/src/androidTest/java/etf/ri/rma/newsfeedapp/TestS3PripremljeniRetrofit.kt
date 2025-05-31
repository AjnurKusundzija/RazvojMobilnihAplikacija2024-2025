package etf.ri.rma.newsfeedapp

import etf.ri.rma.newsfeedapp.data.network.ImagaDAO
import etf.ri.rma.newsfeedapp.data.network.NewsDAO
import etf.ri.rma.newsfeedapp.data.network.api.ImagaApiService
import etf.ri.rma.newsfeedapp.data.network.api.NewsApiService
import okhttp3.OkHttpClient
import retrofit2.Retrofit

class TestS3PripremljenRetrofit {
    fun getNewsDAOwithBaseURL(baseURL:String,httpClient:OkHttpClient): NewsDAO {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseURL)
            .client(httpClient)
            .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
            .build()
        val newsApiService = retrofit.create(NewsApiService::class.java)
        val newsDAO = NewsDAO()
        newsDAO.setApiService(newsApiService)
        return newsDAO
    }
    fun getImaggaDAOwithBaseURL(baseURL:String,httpClient:OkHttpClient):ImagaDAO{
        val imagaDAO = ImagaDAO()
        val retrofit = Retrofit.Builder()
            .baseUrl(baseURL)
            .client(httpClient)
            .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
            .build()
        val imagaApiService = retrofit.create(ImagaApiService::class.java)
        imagaDAO.setApiService(imagaApiService)
        return imagaDAO
    }
}