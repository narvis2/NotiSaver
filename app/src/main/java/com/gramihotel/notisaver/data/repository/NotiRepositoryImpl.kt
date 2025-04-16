package com.gramihotel.notisaver.data.repository

import com.gramihotel.notisaver.data.auth.NotiAuthApiService
import com.gramihotel.notisaver.data.model.base.BaseResponse
import com.gramihotel.notisaver.data.model.noti.NotiCreateRequest
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotiRepositoryImpl @Inject constructor(
    private val notiAuthApiService: NotiAuthApiService
) : NotiRepository {
    override suspend fun createNoti(
        request: NotiCreateRequest
    ): Response<BaseResponse<Unit>> = notiAuthApiService.createNoti(request)
}