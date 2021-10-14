package com.konkuk.kureal.lookup.fragments.one

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.konkuk.kureal.posting.fragments.Article
import com.konkuk.kureal.posting.fragments.one.api.PostingData
import com.konkuk.kureal.posting.fragments.one.api.ResponsePostingData
import com.konkuk.kureal.util.api.RetrofitClient
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OneLookupViewModel(application: Application) : AndroidViewModel(application) {

    var articleData = MutableLiveData<Article>()

    @SuppressLint("CheckResult")
    fun geArticle(pk: Int){ //서버연결
        Log.d("SERVER_LOOKUP","포스트 시작")
        Log.d("LOOKUP_PK",pk.toString())
        RetrofitClient.getApi.getArticle(pk)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({article ->

                articleData.value = Article(article.pk,article.date,article.nickname,
                    article.article,article.photo,article.tag,
                    article.latitude,article.longitude)

                Log.d("RETROFIT_","$articleData")
            },{e ->
                println(e.toString())
            })
    }

}