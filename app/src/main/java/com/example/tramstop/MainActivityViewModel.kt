package com.example.tramstop

import Json4Kotlin_Base
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel


class MainActivityViewModel: ViewModel() {

    val base: LiveData<Json4Kotlin_Base> = Repo.Singleton.base
    val progress: LiveData<Float> = Repo.Singleton.progress

    fun getTimer(context: Context):Repo.Singleton.Tmr{
        return Repo.Singleton.Tmr(context)
    }
}