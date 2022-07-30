package com.example.tramstop

import Json4Kotlin_Base
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData


class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

    val rzebika: LiveData<Json4Kotlin_Base> = Repo.rzebika
    val grunwald: LiveData<Json4Kotlin_Base> = Repo.grunwald

    val progress: LiveData<Float> = Repo.progress

    init {
        Repo.openQueue(getApplication())
    }

    fun getTimer(): Repo.Tmr {
        return Repo.Tmr()
    }

}