package com.gramihotel.notisaver.data.model.noti

data class NotiCreateRequest(
    val hotelId: Long,
    val key: String,
    val postTime: Long,
    val title: String,
    val text: String,
    val notiPlatformType: String,
)
