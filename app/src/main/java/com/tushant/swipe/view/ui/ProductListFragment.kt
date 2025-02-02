package com.tushant.swipe.view.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.tushant.swipe.R
import com.tushant.swipe.databinding.FragmentProductListBinding
import com.tushant.swipe.utils.NetworkUtils.isInternetAvailable
import com.tushant.swipe.utils.Resource
import com.tushant.swipe.view.adapter.ProductAdapter
import com.tushant.swipe.viewModel.ProductViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProductListFragment : Fragment(R.layout.fragment_product_list) {
    private var _binding: FragmentProductListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProductViewModel by viewModel()
    private lateinit var adapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProductListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSearch()
        setupRecyclerView()
        if (isInternetAvailable(requireContext())) {
            viewModel.fetchProducts()
        } else {
            Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT).show()
        }

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.fetchProducts()
        }

        binding.fabAddProduct.setOnClickListener {
            findNavController().navigate(R.id.action_productListFragment_to_addProductFragment)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.filteredProducts.collectLatest { resource ->
                    showLoading(resource is Resource.Loading)
                    when (resource) {
                        is Resource.Loading -> {}

                        is Resource.Success -> {
                            adapter.submitList(resource.data)
                        }

                        is Resource.Error -> {
                            Toast.makeText(
                                requireContext(),
                                "Unable to fetch data.",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.e("ProductListFragment", "onViewCreated: ${resource.message}")
                        }
                    }
                    binding.swipeRefresh.isRefreshing = false
                }
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = ProductAdapter(requireContext())
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@ProductListFragment.adapter
            setHasFixedSize(true)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.shimmerLayout.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.recyclerView.isVisible = !isLoading
        if (isLoading) binding.shimmerLayout.startShimmer() else binding.shimmerLayout.stopShimmer()
    }

    private fun setupSearch() {
        binding.searchEditText.addTextChangedListener(object :
            TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                p0?.toString()?.let { query ->
                    viewLifecycleOwner.lifecycleScope.launch {
                        delay(300)
                        viewModel.searchProducts(query.trim())
                    }
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}