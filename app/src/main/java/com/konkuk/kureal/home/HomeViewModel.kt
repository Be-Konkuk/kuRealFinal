package com.konkuk.kureal.home

import android.annotation.SuppressLint
import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferNetworkLossHandler
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.konkuk.kureal.BuildConfig
import com.konkuk.kureal.home.api.Data
import com.konkuk.kureal.home.article.ArticleInfo
import com.konkuk.kureal.posting.fragments.Article
import com.konkuk.kureal.util.ListLiveData
import com.konkuk.kureal.util.api.RetrofitClient
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    val articleList = ListLiveData<ArticleInfo>()
    private val mContext = getApplication<Application>().applicationContext
    /**
     * S3에 사진 업로드하기
     * local.properties에 aws_accesskey, aws_secret_accesskey 업데이트하기*/
    fun uploadWithTransferUtilty(fileName: String?, file: File?) :String{
        val awsCredentials: AWSCredentials =
            BasicAWSCredentials("${BuildConfig.aws_accesskey}", "${BuildConfig.aws_secret_accesskey}") // IAM 생성하며 받은 것 입력
        val s3Client = AmazonS3Client(awsCredentials, Region.getRegion(Regions.AP_NORTHEAST_2))

        //val s3Client = AmazonS3Client(credentialsProvider)
        val transferUtility =
            TransferUtility.builder().s3Client(s3Client).context(mContext).build()
        TransferNetworkLossHandler.getInstance(mContext)
        val uploadObserver = transferUtility.upload(
            "kureal/photo",
            fileName,
            file
        ) // (bucket api, file이름, file객체)

        uploadObserver.setTransferListener(object : TransferListener {
            override fun onStateChanged(id: Int, state: TransferState) {
                Log.d("MYTAG", "onStateChanged: " + id + ", " + state.toString());
                if (state == TransferState.COMPLETED) {
                    // Handle a completed upload
                }
            }

            override fun onProgressChanged(id: Int, current: Long, total: Long) {
                val done = (current.toDouble() / total * 100.0).toInt()
                Log.d("MYTAG", "UPLOAD - - ID:$id, percent done = $done")
            }

            override fun onError(id: Int, ex: Exception) {
                Log.d("MYTAG", "UPLOAD ERROR - - ID: $id - - EX:$ex")
            }
        })

        var s3URL = s3Client.getResourceUrl("kureal/photo",fileName)
        //Toast.makeText(mContext,s3URL, Toast.LENGTH_LONG).show()
        Log.d("#####S3URL",s3URL)
        return s3URL
    }

    @SuppressLint("CheckResult")
    fun getHomeArticle(s3Url:String){ //서버연결
        Log.d("SERVER_HOME","포스트 시작")
        RetrofitClient.getApi.getHomeArticle(s3Url)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({items ->
                articleList.clear()
                items.data.forEach {
                    articleList.add(ArticleInfo(it.date,it.nickname,it.article,it.pk))
                }
            },{e ->
                println(e.toString())
            })
    }
}