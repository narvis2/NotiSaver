package com.gramihotel.notisaver.data.repository

import com.gramihotel.notisaver.data.model.base.BaseResponse
import com.gramihotel.notisaver.data.model.noti.NotiCreateRequest
import retrofit2.Response

interface NotiRepository {
    suspend fun createNoti(
        request: NotiCreateRequest
    ): Response<BaseResponse<Unit>>
}