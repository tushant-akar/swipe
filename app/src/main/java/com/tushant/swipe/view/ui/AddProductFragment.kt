package com.tushant.swipe.view.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.tushant.swipe.R
import com.tushant.swipe.data.db.SyncWorker
import com.tushant.swipe.data.model.ProductEntity
import com.tushant.swipe.databinding.FragmentAddProductBinding
import com.tushant.swipe.utils.NetworkUtils.isInternetAvailable
import com.tushant.swipe.utils.Resource
import com.tushant.swipe.utils.uriToFile
import com.tushant.swipe.viewModel.ProductViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddProductFragment : BottomSheetDialogFragment(R.layout.fragment_add_product) {
    private var _binding: FragmentAddProductBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProductViewModel by viewModel()
    private var selectedImageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val productTypes = listOf(
            "food",
            "Electronics",
            "samk",
            "andkjnad",
            "sasad",
            "Product",
            "Other",
            "Pr Type",
            "Communication",
            "dadadadad",
            "ddmajldma",
            "dlajdjan",
            "diahad",
            "testing",
            "tesing",
            "type 1"
        )

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            productTypes
        )
        binding.categoryDropDown.setAdapter(adapter)

        val imagePickerLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    result.data?.data?.let { uri ->
                        processImage(uri)
                    }
                }
            }

        binding.btnSelectImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
            imagePickerLauncher.launch(intent)
        }

        binding.btnSubmit.setOnClickListener {
            saveData()
        }

    }

    private fun showLoading(isLoading: Boolean) {
        binding.etProductName.isEnabled = !isLoading
        binding.categoryDropDown.isEnabled = !isLoading
        binding.etPrice.isEnabled = !isLoading
        binding.etTax.isEnabled = !isLoading
        binding.btnSelectImage.isEnabled = !isLoading

        if (isLoading) {
            binding.btnSubmit.visibility = View.GONE
            binding.loading.visibility = View.VISIBLE
            binding.loadingWavy.visibility = View.VISIBLE
        } else {
            binding.loading.visibility = View.GONE
            binding.loadingWavy.visibility = View.GONE
            binding.btnSubmit.visibility = View.VISIBLE
        }
    }

    private fun saveData() {
        val productName = binding.etProductName.text.toString().trim()
        val productType = binding.categoryDropDown.text.toString().trim()
        val price = binding.etPrice.text.toString().trim()
        val tax = binding.etTax.text.toString().trim()

        if (productName.isEmpty()) {
            binding.etProductName.error = "Product name is required"
            return
        }

        if (productType.isEmpty()) {
            binding.categoryDropDown.error = "Product type is required"
            return
        }

        if (price.isEmpty()) {
            binding.etPrice.error = "Price is required"
            return
        }

        if (tax.isEmpty()) {
            binding.etTax.error = "Tax is required"
            return
        }

        val priceValue = price.toDoubleOrNull()
        val taxValue = tax.toDoubleOrNull()
        if (priceValue == null || taxValue == null) {
            Snackbar.make(binding.root, "Invalid price or tax", Snackbar.LENGTH_LONG).show()
            return
        }

        val imagePaths =
            selectedImageUri?.let { listOf(uriToFile(requireContext(), it)) } ?: emptyList()

        val localImagePaths = selectedImageUri?.let { listOf(it.toString()) } ?: emptyList()

        if (isInternetAvailable(requireContext())) {
            viewModel.addProduct(productName, productType, priceValue, taxValue, imagePaths)
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.addProductState.collect {
                    when (it) {
                        is Resource.Success -> {
                            showLoading(false)
                            Toast.makeText(
                                requireContext(),
                                "Product added successfully!",
                                Toast.LENGTH_SHORT
                            ).show()
                            dismiss()
                        }

                        is Resource.Error -> {
                            showLoading(false)
                            Toast.makeText(
                                requireContext(),
                                "Error: ${it.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        is Resource.Loading -> {
                            showLoading(true)
                        }

                        else -> {}
                    }
                }
            }
        } else {
            val productEntity = ProductEntity(
                productName = productName,
                productType = productType,
                price = priceValue,
                tax = taxValue,
                images = localImagePaths
            )
            viewModel.saveProductLocally(productEntity)
            SyncWorker.enqueueSyncWork(requireContext())
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.locallyAddedProducts.collect { localProducts ->
                    if (localProducts.isNotEmpty()) {
                        Toast.makeText(
                            requireContext(),
                            "${localProducts.size} products pending sync.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            Toast.makeText(
                requireContext(),
                "Product saved locally. It will sync when online.",
                Toast.LENGTH_SHORT
            ).show()
            dismiss()
        }

    }

    private fun processImage(uri: Uri) {
        selectedImageUri = uri
        val convertedFile = validateImage(uri)
        if (convertedFile != null) {
            selectedImageUri = uri
            updateSelectedImagesText()
        } else {
            Toast.makeText(
                requireContext(),
                "Only JPEG or PNG images are allowed",
                Toast.LENGTH_SHORT
            )
                .show()

        }

    }

    fun extractFileExtension(imageUri: Uri): String? {
        val context = requireContext()
        val mimeType = context.contentResolver.getType(imageUri)
        return mimeType?.let { mime ->
            MimeTypeMap.getSingleton().getExtensionFromMimeType(mime)
        }
    }

    private fun validateImage(imageUri: Uri): String? {
        val fileExtension = extractFileExtension(imageUri)
        return if (fileExtension != null && (fileExtension.equals("jpg", ignoreCase = true) ||
                    fileExtension.equals("jpeg", ignoreCase = true) ||
                    fileExtension.equals("png", ignoreCase = true))
        ) {
            imageUri.toString()
        } else {
            null
        }
    }

    private fun updateSelectedImagesText() {
        binding.btnSelectImage.text = if (selectedImageUri != null) {
            "Image selected"
        } else {
            "Select Image"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}