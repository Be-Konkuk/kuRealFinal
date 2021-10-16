package com.konkuk.kureal.posting.fragments.one

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
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
import com.konkuk.kureal.posting.fragments.one.api.PostingData
import com.konkuk.kureal.posting.fragments.one.api.ResponsePostingData
import com.konkuk.kureal.util.api.RetrofitClient
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class OnePostViewModel(application: Application) : AndroidViewModel(application) {
    @SuppressLint("StaticFieldLeak")
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
        Toast.makeText(mContext,s3URL, Toast.LENGTH_LONG).show()
        Log.d("#####S3URL",s3URL)
        return s3URL
    }

    fun posting(postingData: PostingData){ //서버연결
        Log.d("SERVER_ONBOARD4","포스트 시작")
        Log.d("***SERVER_DATA",postingData.date+","+postingData.nickname+","+postingData.article+","+postingData.photo+","+postingData.tag+","+postingData.latitude+","+postingData.longitude)
        val call: Call<ResponsePostingData> = RetrofitClient.getApi.postArticle(postingData)
        call.enqueue(object : Callback<ResponsePostingData> {
            override fun onResponse(
                call: Call<ResponsePostingData>,
                response: Response<ResponsePostingData>
            ){
                Log.d("SERVER_ONBOARD44",response.body()?.status.toString())
                Log.d("SERVER_ONBOARD44",response.body()?.message.toString())
            }
            override fun onFailure(call: Call<ResponsePostingData>, t: Throwable) {
                Log.d("SERVER_ONBOARD4","포스트 실패" + t.message)
            }
        })
    }
}