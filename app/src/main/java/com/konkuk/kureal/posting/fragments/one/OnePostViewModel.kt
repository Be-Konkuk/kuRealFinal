package com.konkuk.kureal.posting.fragments.one

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel

class OnePostViewModel(application: Application) : AndroidViewModel(application) {
    @SuppressLint("StaticFieldLeak")
    private val mContext = getApplication<Application>().applicationContext


}