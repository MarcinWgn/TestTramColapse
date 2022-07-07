package com.example.tramstop

import Json4Kotlin_Base
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel


class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

    val base: LiveData<Json4Kotlin_Base> = Repo.base
    val progress: LiveData<Float> = Repo.progress

    init {
        Repo.openQueue(getApplication())
    }

    fun getTimer():Repo.Tmr{
        return Repo.Tmr(getApplication())
    }

}