package com.tushant.swipe.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.tushant.swipe.R
import com.tushant.swipe.data.model.ProductEntity
import com.tushant.swipe.databinding.ItemProductBinding

class ProductEntityAdapter(private val context: Context) :
    ListAdapter<ProductEntity, ProductEntityAdapter.ProductViewHolder>(ProductEntityDiffCallback()) {

    inner class ProductViewHolder(private val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: ProductEntity) {
            binding.productName.text = product.productName
            binding.productType.text = product.productType
            binding.productPrice.text = "₹ ${product.price}"
            binding.productTax.text = "Tax: ${product.tax}%"

            Glide.with(context)
                .load(product.images?.get(0))
                .placeholder(R.drawable.baseline_image_24)
                .error(R.drawable.baseline_image_24)
                .fallback(R.drawable.baseline_image_24)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.productImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding =
            ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class ProductEntityDiffCallback : DiffUtil.ItemCallback<ProductEntity>() {
    override fun areItemsTheSame(oldItem: ProductEntity, newItem: ProductEntity): Boolean {
        return oldItem.productName == newItem.productName
    }

    override fun areContentsTheSame(oldItem: ProductEntity, newItem: ProductEntity): Boolean {
        return oldItem == newItem
    }
}