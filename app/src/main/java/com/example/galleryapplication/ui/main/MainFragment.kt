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
import androidx.recyclerview.widget.RecyclerView
import com.example.galleryapplication.ImagesGallery
import com.example.galleryapplication.adapter.GalleryAdapter
import com.example.galleryapplication.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    lateinit var recyclerView: RecyclerView
    lateinit var galleryAdapter: GalleryAdapter
    var images = mutableListOf<String>()

    val READ_PERMISSION_CODE = 101


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment

        val binding = FragmentMainBinding.inflate(inflater)

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
            loadImages()
        }





        return binding.root
    }

    fun loadImages(){
        recyclerView.setHasFixedSize(true)
        images = ImagesGallery.listOfImages(requireContext())

        galleryAdapter = GalleryAdapter(requireContext(), images, GalleryAdapter.OnClickListener{
            Toast.makeText(requireContext(), "CLICKED", Toast.LENGTH_SHORT).show()
        })
        recyclerView.adapter = galleryAdapter

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == READ_PERMISSION_CODE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(requireContext(), "READ STORAGE GRANTED", Toast.LENGTH_SHORT).show()
                loadImages()
            } else {
                Toast.makeText(requireContext(), "READ STORAGE DENIED", Toast.LENGTH_SHORT).show()

            }
        }
    }


}