package com.example.tramstop

import Json4Kotlin_Base
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request.Method.GET
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import java.util.*
import kotlin.concurrent.timerTask

object Repo {
    val TAG = "tst"
    private val _base = MutableLiveData<Json4Kotlin_Base>()
    val base = _base
    private val _progress = MutableLiveData<Float>(0f)
    val progress = _progress
    private final lateinit var queue: RequestQueue

    fun openQueue(context: Context){
        queue = Volley.newRequestQueue(context)
    }
    val rzebikaUrl = "http://www.ttss.krakow.pl/internetservice/services/passageInfo/stopPassages/stop?stop=1262"
    val grunwaldzkieUrl = "http://www.ttss.krakow.pl/internetservice/services/passageInfo/stopPassages/stop?stop=3338"

    val RzebikaJsonStringRequest = StringRequest(
        GET,rzebikaUrl,
        { response ->
            convertToGson(response)  },
        { error ->
            Log.d(TAG, error.toString())
        })
    val GrunwadzkieJsonStringRequest = StringRequest(
        GET, grunwaldzkieUrl,
        { response ->
            convertToGson(response)  },
        { error ->
            Log.d(TAG, error.toString())
        })


    fun jsonRequest() {
        Log.d(TAG,"json request")
            queue.add(RzebikaJsonStringRequest)
        // TODO: json request

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
                    if(timeFrag==0)jsonRequest()
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