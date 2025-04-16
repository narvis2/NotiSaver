package com.gramihotel.notisaver.data.auth

import com.gramihotel.notisaver.data.model.base.BaseResponse
import com.gramihotel.notisaver.data.model.noti.NotiCreateRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface NotiAuthApiService {

    @POST("noti")
    suspend fun createNoti(
        @Body request: NotiCreateRequest
    ): Response<BaseResponse<Unit>>
}