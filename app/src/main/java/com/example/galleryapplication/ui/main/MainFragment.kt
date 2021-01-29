package com.example.galleryapplication.ui.main

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.galleryapplication.adapter.GalleryAdapter
import com.example.galleryapplication.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    val viewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }

    lateinit var recyclerView: RecyclerView
    lateinit var galleryAdapter: GalleryAdapter

    val READ_PERMISSION_CODE = 101


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment

        val binding = FragmentMainBinding.inflate(inflater)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.images.observe(viewLifecycleOwner, { images ->
            if (images != null) {
                galleryAdapter = GalleryAdapter(requireContext(), images, GalleryAdapter.OnClickListener {
                    viewModel.displayImage(it)
                })
                binding.recyclerViewGalleryImages.adapter = galleryAdapter
                galleryAdapter.notifyDataSetChanged()
            }
        })

        recyclerView = binding.recyclerViewGalleryImages


        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                READ_PERMISSION_CODE
            )
        } else {
            viewModel.listImages(requireContext())
        }

        viewModel.navigateToSelectedImage.observe(viewLifecycleOwner, {
            if(it != null){
                this.findNavController().navigate(MainFragmentDirections.actionMainFragmentToImageFragment4(it))
                viewModel.displayImageCompleted()

            }
        })


        return binding.root
    }


//    fun loadImages() {
//        recyclerView.setHasFixedSize(true)
//        images = ImagesGallery.listOfImages(requireContext())
//
//        galleryAdapter = GalleryAdapter(requireContext(), images, GalleryAdapter.OnClickListener {
//            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
//        })
//        recyclerView.adapter = galleryAdapter
//    }

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


}