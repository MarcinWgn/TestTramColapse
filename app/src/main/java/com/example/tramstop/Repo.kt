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
//    val RZEBIKA = 0
//    val GRUNWALDZKIE = 1
    val TAG = "tst"

    enum class Stop(val id: Int, val idStop: String){
        rzebika(0, "1262"),
        grunwald(1, "3338"),
        wolnica(2, "360")
    }

    private val _rzebika = MutableLiveData<Response>()
    val rzebika: LiveData<Response> = _rzebika

    private val _grunwald = MutableLiveData<Response>()
    val grunwald: LiveData<Response> = _grunwald

    private val _wolnica= MutableLiveData<Response>()
    val wolnica: LiveData<Response> = _wolnica



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




    fun getUrl(stopId: String) = Uri.Builder().scheme("https")
        .authority("www.ttss.krakow.pl")
        .appendPath("internetservice")
        .appendPath("services")
        .appendPath("passageInfo")
        .appendPath("stopPassages")
        .appendPath("stop")
        .appendQueryParameter("stop", stopId)
        .appendQueryParameter("timeFrame","60")
        .build().toString()

    val RzebikaJsonStringRequest = StringRequest(
        GET, getUrl(Stop.rzebika.idStop),
        { response ->
            _rzebika.value = convertToGson(response)

        },
        { error ->
            Log.d(TAG, error.toString())
        })
    val GrunwadzkieJsonStringRequest = StringRequest(
        GET, getUrl(Stop.grunwald.idStop),
        { response ->
            _grunwald.value = convertToGson(response)

        },
        { error ->
            Log.d(TAG, error.toString())
        })

    val WolnicaJsonStringRequest = StringRequest(
        GET, getUrl(Stop.wolnica.idStop),
        { response ->
            _wolnica.value = convertToGson(response)

        },
        { error ->
            Log.d(TAG, error.toString())
        })



    fun jsonRequest() {
        Log.d(TAG, "request: ${getUrl(Stop.rzebika.idStop)}")

        when (state.value){
            Stop.rzebika.id -> queue.add(RzebikaJsonStringRequest)
            Stop.grunwald.id -> queue.add(GrunwadzkieJsonStringRequest)
            Stop.wolnica.id -> queue.add(WolnicaJsonStringRequest)
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