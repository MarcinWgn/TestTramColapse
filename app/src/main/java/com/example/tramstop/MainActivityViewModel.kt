package com.example.tramstop

import Json4Kotlin_Base
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel


class MainActivityViewModel: ViewModel() {

    val base: LiveData<Json4Kotlin_Base> = Repo.base
    val progress: LiveData<Float> = Repo.progress

    fun getTimer(context: Context):Repo.Tmr{
        return Repo.Tmr(context)
    }
}