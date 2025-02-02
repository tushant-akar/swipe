package com.tushant.swipe.data.model

import com.google.gson.annotations.SerializedName

data class AddProductResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("product_details")
    val productDetails: Product,
    @SerializedName("product_id")
    val productId: Int,
    @SerializedName("success")
    val success: Boolean
)
