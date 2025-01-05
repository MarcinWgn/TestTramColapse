package com.example.tramstop

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData


class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

    val rzebika: LiveData<Response> = Repo.rzebika
    val grunwald: LiveData<Response> = Repo.grunwald

    val progress: LiveData<Float> = Repo.progress

    init {
        Repo.openQueue(getApplication())
    }

    fun getTimer(): Repo.Tmr {
        return Repo.Tmr()
    }

}