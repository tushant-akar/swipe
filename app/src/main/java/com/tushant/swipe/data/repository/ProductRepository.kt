package com.tushant.swipe.data.repository

import android.content.Context
import android.util.Log
import androidx.core.net.toUri
import com.tushant.swipe.data.db.ProductDao
import com.tushant.swipe.data.model.AddProductResponse
import com.tushant.swipe.data.model.Product
import com.tushant.swipe.data.model.ProductEntity
import com.tushant.swipe.data.remote.ProductService
import com.tushant.swipe.utils.Resource
import com.tushant.swipe.utils.uriToFile
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response

class ProductRepository(
    private val productService: ProductService,
    private val productDao: ProductDao
) {
    private val _products = MutableStateFlow<Resource<List<Product>>>(Resource.Loading())
    val products: StateFlow<Resource<List<Product>>> = _products

    suspend fun fetchProducts() {
        _products.value = Resource.Loading()
        try {
            val response: Response<List<Product>> = productService.getProducts()
            if (response.isSuccessful && response.body() != null) {
                _products.value = Resource.Success(response.body()!!)
            } else {
                _products.value = Resource.Error("Failed to fetch products: ${response.message()}")
            }
        } catch (e: Exception) {
            _products.value = Resource.Error(e.message ?: "An unknown error occurred")
        }
    }

    suspend fun addProduct(
        productName: RequestBody,
        productType: RequestBody,
        price: RequestBody,
        tax: RequestBody,
        files: List<MultipartBody.Part?>? = null
    ): Resource<AddProductResponse> {
        return try {
            val response: Response<AddProductResponse> = productService.addProduct(
                productName, productType, price, tax, files
            )
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error("Failed to add product: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unknown error occurred")
        }
    }

    suspend fun saveProductLocally(product: ProductEntity) {
        try {
            productDao.insertProduct(product)
        } catch (e: Exception) {
            Log.e("ProductRepository", "Failed to save product locally: ${e.message}")
        }
    }

    suspend fun getAllLocalProducts(): List<ProductEntity> {
        return try {
            productDao.getAllProductsSync()
        } catch (e: CancellationException) {
            Log.w("ProductRepository", "getAllLocalProducts: Job cancelled")
            emptyList()
        } catch (e: Exception) {
            Log.e("ProductRepository", "Failed to get local products: ${e.message}")
            emptyList()
        }
    }

    suspend fun syncProducts(context: Context) {
        val localProducts = productDao.getAllProductsSync()
        localProducts.forEach { product ->
            val response = addProduct(
                productName = product.productName.toRequestBody("text/plain".toMediaTypeOrNull()),
                productType = product.productType.toRequestBody("text/plain".toMediaTypeOrNull()),
                price = product.price.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
                tax = product.tax.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
                files = product.images?.map {
                    val uri = it.toUri()
                    val file = uriToFile(context, uri)
                    file?.let {
                        MultipartBody.Part.createFormData(
                            name = "files[]",
                            filename = file.name,
                            body = file.asRequestBody("image/jpg".toMediaTypeOrNull())
                        )
                    }
                }
            )
            if (response is Resource.Success) {
                productDao.deleteProduct(product)
            }
        }
    }
}