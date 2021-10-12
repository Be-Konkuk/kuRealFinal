package com.konkuk.kureal.lookup

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class LookupViewModel(application: Application) : AndroidViewModel(application) {
    val id = MutableLiveData<String>()


}