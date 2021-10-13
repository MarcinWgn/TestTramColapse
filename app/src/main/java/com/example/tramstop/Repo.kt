package com.example.tramstop

import Json4Kotlin_Base
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import java.util.*
import kotlin.concurrent.timerTask

public class Repo() {

    companion object{
        val TAG = "tst"
    }
    object Singleton {

        private val _base = MutableLiveData<Json4Kotlin_Base>()
        val base = _base

        private val _progress = MutableLiveData<Float>(0f)
        val progress = _progress

        fun jsonRequest(context: Context) {
            val queue = Volley.newRequestQueue(context)
            val url =
                "http://www.ttss.krakow.pl/internetservice/services/passageInfo/stopPassages/stop?stop=1262"
            val jsonStringRequest = StringRequest(Request.Method.GET,url,
                { response ->
                    convertToGson(response)  },
                { error ->
                    Log.d("Volley", error.toString())  })
            queue.add(jsonStringRequest)
        }
        private fun convertToGson(json: String){
            val data = Gson().fromJson(json, Json4Kotlin_Base::class.java)
            _base.value = data
        }

        class Tmr(context: Context){
            private val timer: Timer
            init {
                var timeFrag = 0
                timer = Timer()
                timer.scheduleAtFixedRate(timerTask {
                    if(timeFrag==0)jsonRequest(context)
                    timeFrag++
                    if(timeFrag==100)timeFrag= 0
                    _progress.postValue(timeFrag.toFloat()/100)

                },0,300)
            }
            fun cancel(){
                timer.cancel()
            }
        }

    }

}