package com.abdelmageed.chefatask.data.modules.remote.dto

import com.google.gson.annotations.SerializedName

data class BaseErrorResponse(

	@field:SerializedName("code")
	val code: String? = null,

	@field:SerializedName("message")
    var message: String? = null,

	val status: Int? = null
)
