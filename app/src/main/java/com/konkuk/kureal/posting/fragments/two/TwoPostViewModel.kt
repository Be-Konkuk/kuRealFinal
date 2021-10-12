package com.konkuk.kureal.posting.fragments.two

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel

class TwoPostViewModel(application: Application) : AndroidViewModel(application) {
    @SuppressLint("StaticFieldLeak")
    private val mContext = getApplication<Application>().applicationContext


}