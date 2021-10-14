package com.konkuk.kureal.util.api

import com.konkuk.kureal.lookup.fragments.one.api.LookupData
import com.konkuk.kureal.posting.fragments.one.api.PostingData
import com.konkuk.kureal.posting.fragments.one.api.ResponsePostingData
import io.reactivex.Single
import retrofit2.Call
import retrofit2.http.*

interface RetrofitService {

    //글 포스팅
    @POST("api/posting")
    fun postArticle(
        @Body body: PostingData
    ) : Call<ResponsePostingData>

    //글 조회
    @GET("api/posting/{pk}")
    fun getArticle(
        @Path("pk") pk:Int
    ) : Single<LookupData>
}