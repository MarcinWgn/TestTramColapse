package com.example.tramstop

import Json4Kotlin_Base
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
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

    private val _rzebika = MutableLiveData<Json4Kotlin_Base>()
    val rzebika: LiveData<Json4Kotlin_Base> = _rzebika

    private val _grunwald = MutableLiveData<Json4Kotlin_Base>()
    val grunwald: LiveData<Json4Kotlin_Base> = _grunwald

    private val _state = MutableLiveData(true)
    val state: LiveData<Boolean> = _state


    private val _progress = MutableLiveData<Float>(0f)
    val progress = _progress
    private lateinit var queue: RequestQueue

    fun openQueue(context: Context) {
        queue = Volley.newRequestQueue(context)
    }

    fun setState(state: Boolean){
        _state.value = state
    }

    const val arrival = "arrival"
    const val departure = "departure"
    const val plaszow = "Mały Płaszów P+R"
    val rzebikaStop = "1262"
    val grunwaldStop = "3338"

    fun getUrl(stopId: String) = Uri.Builder().scheme("http")
            .authority("www.ttss.krakow.pl")
            .appendPath("internetservice")
            .appendPath("services")
            .appendPath("passageInfo")
            .appendPath("stopPassages")
            .appendPath("stop")
            .appendQueryParameter("stop",stopId)
            .build().toString()

    val RzebikaJsonStringRequest = StringRequest(
        GET, getUrl(rzebikaStop),
        { response ->
            _rzebika.value = convertToGson(response)
        },
        { error ->
            Log.d(TAG, error.toString())
        })
    val GrunwadzkieJsonStringRequest = StringRequest(
        GET, getUrl(grunwaldStop),
        { response ->
            _grunwald.value=convertToGson(response)
        },
        { error ->
            Log.d(TAG, error.toString())
        })

    fun jsonRequest() {
        if(state.value == true){
            queue.add(RzebikaJsonStringRequest)
        }else
            queue.add(GrunwadzkieJsonStringRequest)
    }


    private fun convertToGson(json: String): Json4Kotlin_Base {
        return Gson().fromJson(json, Json4Kotlin_Base::class.java)
    }

    class Tmr(context: Context) {
        private val timer: Timer

        init {
            var timeFrag = 0
            timer = Timer()
            timer.scheduleAtFixedRate(timerTask {
                if (timeFrag == 0) jsonRequest()
                timeFrag++
                if (timeFrag == 100) timeFrag = 0
                _progress.postValue(timeFrag.toFloat() / 100)

            }, 0, 300)
            state.observeForever {
                jsonRequest()
            }
        }

        fun cancel() {
            timer.cancel()
        }
    }
}