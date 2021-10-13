package com.konkuk.kureal.util.api

import com.konkuk.kureal.posting.fragments.one.api.PostingData
import com.konkuk.kureal.posting.fragments.one.api.ResponsePostingData
import io.reactivex.Single
import retrofit2.Call
import retrofit2.http.*

interface RetrofitService {

    //글 포스팅
    @POST("api/posting")
    fun postPosting(
        @Body body: PostingData
    ) : Call<ResponsePostingData>

}