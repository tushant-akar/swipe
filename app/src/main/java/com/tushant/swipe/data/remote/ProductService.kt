package com.tushant.swipe.data.remote

import com.tushant.swipe.data.model.AddProductResponse
import com.tushant.swipe.data.model.Product
import com.tushant.swipe.utils.Constants.ADD_PRODUCT_ENDPOINT
import com.tushant.swipe.utils.Constants.GET_PRODUCTS_ENDPOINT
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ProductService {
    @GET(GET_PRODUCTS_ENDPOINT)
    suspend fun getProducts(): Response<List<Product>>

    @Multipart
    @POST(ADD_PRODUCT_ENDPOINT)
    suspend fun addProduct(
        @Part("product_name") productName: RequestBody,
        @Part("product_type") productType: RequestBody,
        @Part("price") price: RequestBody,
        @Part("tax") tax: RequestBody,
        @Part files: List<MultipartBody.Part?>? = null
    ): Response<AddProductResponse>
}