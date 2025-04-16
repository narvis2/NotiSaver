package com.gramihotel.notisaver.data.model.base

data class BaseResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val resultMsg: String? = null,
    val code: Int
)
