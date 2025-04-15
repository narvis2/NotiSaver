package com.gramihotel.notisaver.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.gramihotel.notisaver.data.auth.NotiAuthApiService
import com.gramihotel.notisaver.data.model.noti.NotiCreateRequest
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class NotiWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val notiAuthApiService: NotiAuthApiService,
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {

        val key = inputData.getString("key")
        val notiPlatformType = inputData.getString("notiPlatformType")
        val postTime = inputData.getLong(
            "postTime",
            defaultValue = 0
        )
        val title = inputData.getString("title")
        val text = inputData.getString("text")

        if (key.isNullOrBlank() || notiPlatformType.isNullOrBlank() || title.isNullOrBlank() || text.isNullOrBlank()) {
            return Result.failure()
        }

        return try {
            val response = notiAuthApiService.createNoti(NotiCreateRequest(
                hotelId = 1,
                key = key,
                postTime = postTime,
                title = title,
                text = text,
                notiPlatformType = notiPlatformType
            ))

            val data = response.body()

            if (response.isSuccessful && data != null && data.success) {
                return Result.success()
            }

            Result.retry()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }
}