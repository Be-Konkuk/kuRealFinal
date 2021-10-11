package com.konkuk.kureal.home.fragments.four

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel

class FourHomeViewModel(application: Application) : AndroidViewModel(application) {
    @SuppressLint("StaticFieldLeak")
    private val mContext = getApplication<Application>().applicationContext


}