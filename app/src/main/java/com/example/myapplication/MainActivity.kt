package com.example.myapplication

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import java.io.IOException


class MainActivity : AppCompatActivity(){
    var urlsDBHelper = UrlDBHelper(this)
    var client = OkHttpClient();
    var ALL_URLS = ArrayList<DtoUrlData>();


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val SDK_INT = Build.VERSION.SDK_INT
        if (SDK_INT > 8) {
            val policy = ThreadPolicy.Builder()
                .permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
        setContentView(R.layout.activity_main)
    }

    fun btnAddUser_Click(v:View) {
        this.addUser()
    }
    fun btnDeleteUser_Click(v:View) {
        this.deleteUser()
    }
    fun doApiCall_Click(v:View) {
        this.doApiCall()
    }
    fun btnDropDB_Click(v:View){
        this.dropDB()
    }
    fun btnShowAllUsersFromDB_Click(v:View){
        this.showAllUsersFromDB()
    }

    fun fetchUsersFromApi(){
        val url  =""
        val textViewResult = findViewById<TextView>(R.id.textview_result)
        textViewResult.setText("Fetching data from "+ url)
        val request = Request.Builder().url(url).build()
        val call =  client.newCall(request).enqueue(responseCallback = object : Callback {
            var mainHandler = Handler(this@MainActivity.mainLooper)
            @RequiresApi(Build.VERSION_CODES.P)
            override fun onResponse(call: Call, response: Response) {
                mainHandler.post {

                    val postBody = response.body?.string()
                    if (postBody == null) return@post
                  //  val gson = Gson()
                    //var urlInformationObject = gson.fromJson(postBody, DtoUrlInfo::class.java)
                    runOnUiThread(Runnable { // (1)
                        try {


                            val gson = Gson()
                            var urlInformationObject = gson.fromJson(postBody, Array<DtoUrlData>::class.java)
                            ALL_URLS = urlInformationObject.toList() as ArrayList<DtoUrlData>

                            textViewResult.setText("Success")
                            this@MainActivity.SaveUsersToDB()
                        } catch (e:Exception) {
                        // handler
                            println("g")
                    } finally {
                        // optional finally block
                    }
                    })
                }
            }
            override fun onFailure(call: Call, e: IOException) {
                println("API execute failed")
            }
        })
    }

    fun RefreshUserInterface(msg:String){
        val textViewResult = findViewById<TextView>(R.id.textview_result)
        val linearLayoutView = findViewById<LinearLayout>(R.id.ll_entries)
        linearLayoutView.removeAllViews()
        this.showAllUsersFromDB()
        textViewResult.setText(msg)
    }

    fun SaveUsersToDB(){
        ALL_URLS.forEach {
            var result =this.urlsDBHelper.insertUrl(UrlModel(urlId = it.id.toString() ,url = it.url.toString()))
            RefreshUserInterface("URL user : "+result.toString())
        }
    }

    fun addUser(){
        val urlid = findViewById<EditText>(R.id.edittext_userid).getText().toString()
        val url = findViewById<EditText>(R.id.edittext_name).getText().toString()

        var result =this.urlsDBHelper.insertUrl(UrlModel(urlId = urlid,url = url))
        RefreshUserInterface("URL added : "+result.toString())
    }

    fun doApiCall(){
        fetchUsersFromApi()
        SaveUsersToDB()
    }

    fun deleteUser() {
        val urlid = findViewById<EditText>(R.id.edittext_userid).getText().toString()
        val result = urlsDBHelper.deleteUrl(urlid)
        RefreshUserInterface("Deleted URL : " + result)
    }

    fun dropDB(){
        val result = urlsDBHelper.deleteDB()
        RefreshUserInterface("DROPPED DATABASE: "+result)
    }

    fun showAllUsersFromDB(){
        val textViewResult = findViewById<TextView>(R.id.textview_result)
        val linearLayoutView = findViewById<LinearLayout>(R.id.ll_entries)
        var urls = urlsDBHelper.readAllUrls()

        linearLayoutView.removeAllViews()

        urls.forEach {
            var tv_user = TextView(this)
            tv_user.textSize = 30F
            tv_user.text = it.urlId.toString() + " - " +it.url.toString()

            linearLayoutView.addView(tv_user)
        }
        textViewResult.setText("Fetched " + urls.size + " URLS")
    }

    fun ShowAllUsersFrom_ALLUSERS_ARRAY(){
        val textViewResult = findViewById<TextView>(R.id.textview_result)
        val linearLayoutView = findViewById<LinearLayout>(R.id.ll_entries)

        var msg = ""
        linearLayoutView.removeAllViews()

        ALL_URLS.forEach {
            var tv_user = TextView(this)
            tv_user.textSize = 30F
            tv_user.text = it.id.toString() + " - " +it.url.toString()

            linearLayoutView.addView(tv_user)
        }
        textViewResult.setText("Fetched " + ALL_URLS.size + " URLS")
    }
}