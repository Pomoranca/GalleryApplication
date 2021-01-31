package com.example.galleryapplication.ui.main

import android.Manifest
import android.content.ClipboardManager
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import coil.Coil
import coil.ImageLoader
import coil.request.ImageRequest
import com.example.galleryapplication.*
import com.example.galleryapplication.adapter.GalleryAdapter
import com.example.galleryapplication.databinding.FragmentMainBinding
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class MainFragment : Fragment() {

    private lateinit var imageLoader: ImageLoader
    lateinit var editTextUrl: EditText
    lateinit var progressbar: ProgressBar

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    private val viewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }

    lateinit var recyclerView: RecyclerView
    lateinit var galleryAdapter: GalleryAdapter

    val READ_PERMISSION_CODE = 101


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {



        val binding = FragmentMainBinding.inflate(inflater)


        editTextUrl = binding.editTextUrl
        progressbar = binding.progressbar

        editTextUrl.addTextChangedListener {
            binding.buttonDownload.enable(it.toString().isNotEmpty())
        }

        binding.viewModel = viewModel
        binding.lifecycleOwner = this


        progressbar.visible(false)
        imageLoader = Coil.imageLoader(requireContext())


        binding.buttonDownload.setOnClickListener {
            checkPermissionAndDownloadBitmap(editTextUrl.text.toString().trim())
            Log.i("TEXTS", editTextUrl.text.toString())
        }

        binding.buttonPaste.setOnClickListener {
            pasteLink()
        }

        setPermissionCallback()

        viewModel.images.observe(viewLifecycleOwner) { images ->
            galleryAdapter =
                GalleryAdapter(requireContext(), images, GalleryAdapter.OnClickListener {
                    viewModel.displayImage(it)
                })
            binding.recyclerViewGalleryImages.adapter = galleryAdapter
            galleryAdapter.notifyDataSetChanged()
        }


        recyclerView = binding.recyclerViewGalleryImages


        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                READ_PERMISSION_CODE
            )
        } else {
            viewModel.listImages(requireContext())
        }

        viewModel.navigateToSelectedImage.observe(viewLifecycleOwner) {
            if (it != null) {
                this.findNavController()
                    .navigate(MainFragmentDirections.actionMainFragmentToImageFragment4(it))
                viewModel.displayImageCompleted()

            }
        }


        return binding.root
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == READ_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(requireContext(), "READ STORAGE GRANTED", Toast.LENGTH_SHORT).show()
                viewModel.listImages(requireContext())
            } else {
                Toast.makeText(requireContext(), "READ STORAGE DENIED", Toast.LENGTH_SHORT).show()

            }
        }
    }

    private fun setPermissionCallback() {
        requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    getBitmapFromUrl(editTextUrl.text.toString().trim())
                }
            }
    }


    private fun getBitmapFromUrl(bitmapURL: String) = lifecycleScope.launch {
        progressbar.visible(true)
        val request = ImageRequest.Builder(requireActivity())
            .data(bitmapURL)
            .build()
        try {
            val downloadedBitmap = (imageLoader.execute(request).drawable as BitmapDrawable).bitmap
            saveMediaToStorage(downloadedBitmap)
        } catch (e: Exception) {
            requireActivity().toast(e.message)
        }
        progressbar.visible(false)
    }


    private fun checkPermissionAndDownloadBitmap(bitmapURL: String) {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                getBitmapFromUrl(bitmapURL)
            }

            shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE) -> {
                requireActivity().showPermissionRequestDialog(
                    getString(R.string.permission_title),
                    getString(R.string.write_permission_request)
                ) {
                    requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
    }

    private fun saveMediaToStorage(bitmap: Bitmap) {
        val filename = "${System.currentTimeMillis()}.jpg"
        var fos: OutputStream? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            requireActivity().contentResolver?.also { resolver ->
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }
                val imageUri: Uri? =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        } else {
            val imagesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, filename)
            fos = FileOutputStream(image)
        }
        fos?.use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            requireActivity().toast("Saved to Photos")
        }
    }

    private fun pasteLink() {
        val clipboard: ClipboardManager? =
            requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
        if (clipboard?.hasPrimaryClip() == true) {
            editTextUrl.setText(clipboard.primaryClip?.getItemAt(0)?.text.toString())
        }
    }

    //TODO update recyclerView on image download



}