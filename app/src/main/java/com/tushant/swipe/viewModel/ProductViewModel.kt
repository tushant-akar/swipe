package com.tushant.swipe.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tushant.swipe.data.model.AddProductResponse
import com.tushant.swipe.data.model.Product
import com.tushant.swipe.data.model.ProductEntity
import com.tushant.swipe.data.repository.ProductRepository
import com.tushant.swipe.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File


class ProductViewModel(private val repository: ProductRepository) : ViewModel() {

    val products: StateFlow<Resource<List<Product>>> = repository.products

    private val _searchQuery = MutableStateFlow("")
    val filteredProducts = combine(products, _searchQuery) { productsResource, query ->
        when (productsResource) {
            is Resource.Success -> {
                Resource.Success(filterProducts(productsResource.data ?: emptyList(), query))
            }

            is Resource.Loading -> Resource.Loading()
            is Resource.Error -> Resource.Error(productsResource.message.orEmpty())
        }
    }

    private val _addProductState = MutableStateFlow<Resource<AddProductResponse>?>(null)
    val addProductState: StateFlow<Resource<AddProductResponse>?> = _addProductState

    private val _locallyAddedProducts = MutableStateFlow<List<ProductEntity>>(emptyList())
    val locallyAddedProducts: StateFlow<List<ProductEntity>> = _locallyAddedProducts

    fun fetchProducts() {
        viewModelScope.launch {
            repository.fetchProducts()
        }
    }

    fun searchProducts(query: String) {
        _searchQuery.value = query.trim()
    }

    private fun filterProducts(products: List<Product>, query: String): List<Product> {
        return if (query.isEmpty()) {
            products
        } else {
            products.filter { product ->
                (product.productName).contains(query, ignoreCase = true) ||
                        (product.productType).contains(query, ignoreCase = true)
            }
        }
    }

    fun addProduct(
        productName: String,
        productType: String,
        price: Double,
        tax: Double,
        images: List<File?> = emptyList()
    ) {
        viewModelScope.launch {
            _addProductState.value = Resource.Loading()

            val productNameRequestBody = productName.toRequestBody("text/plain".toMediaTypeOrNull())
            val productTypeRequestBody = productType.toRequestBody("text/plain".toMediaTypeOrNull())
            val priceRequestBody = price.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val taxRequestBody = tax.toString().toRequestBody("text/plain".toMediaTypeOrNull())

            val imageParts = images.map { file ->
                file?.let {
                    MultipartBody.Part.createFormData(
                        name = "files[]",
                        filename = file.name,
                        body = file.asRequestBody("image/jpg".toMediaTypeOrNull())
                    )
                }
            }
            val response = repository.addProduct(
                productNameRequestBody,
                productTypeRequestBody,
                priceRequestBody,
                taxRequestBody,
                imageParts
            )
            _addProductState.value = response
        }
    }

    fun saveProductLocally(product: ProductEntity) {
        viewModelScope.launch {
            repository.saveProductLocally(product)
            getLocalProducts()
        }
    }

    private fun getLocalProducts() {
        viewModelScope.launch {
            _locallyAddedProducts.value = repository.getAllLocalProducts()
        }
    }
}
