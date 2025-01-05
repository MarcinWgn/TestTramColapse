package com.example.tramstop

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
import java.util.Timer
import kotlin.concurrent.timerTask

object Repo {
    val RZEBIKA = 0
    val GRUNWALDZKIE = 1
    val TAG = "tst"

    private val _rzebika = MutableLiveData<Response>()
    val rzebika: LiveData<Response> = _rzebika

    private val _grunwald = MutableLiveData<Response>()
    val grunwald: LiveData<Response> = _grunwald

    private val _state = MutableLiveData(0)
    val state: LiveData<Int> = _state


    private val _progress = MutableLiveData<Float>(0f)
    val progress = _progress
    private lateinit var queue: RequestQueue

    fun openQueue(context: Context) {
        queue = Volley.newRequestQueue(context)
    }

    fun setState(state: Int) {
        _state.value = state
    }

    val rzebikaStop = "1262"
    val grunwaldStop = "3338"

    fun getUrl(stopId: String) = Uri.Builder().scheme("https")
        .authority("www.ttss.krakow.pl")
        .appendPath("internetservice")
        .appendPath("services")
        .appendPath("passageInfo")
        .appendPath("stopPassages")
        .appendPath("stop")
        .appendQueryParameter("stop", stopId)
        .appendQueryParameter("timeFrame","50")
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
            _grunwald.value = convertToGson(response)

        },
        { error ->
            Log.d(TAG, error.toString())
        })

    fun jsonRequest() {
        Log.d(TAG, "request: ${getUrl(rzebikaStop)}")
        if (state.value == RZEBIKA) {
            queue.add(RzebikaJsonStringRequest)
        } else{
            queue.add(GrunwadzkieJsonStringRequest)
        }

    }

    private fun convertToGson(json: String): Response {
        Log.d(TAG, "convert to json ${state.value}")
        return Gson().fromJson(json, Response::class.java)
    }

    class Tmr() {
        private val timer: Timer

        init {
            var timeFrag = 0
            timer = Timer()
            timer.schedule(timerTask {
                timeFrag++
                if (timeFrag == 100){
                    jsonRequest()
                    timeFrag = 0
                }
                _progress.postValue(timeFrag.toFloat() / 100)

            }, 0, 300)
            state.observeForever {
                jsonRequest()
                Log.d(TAG, "observe")
            }
        }

        fun cancel() {
            timer.cancel()
        }
    }
}