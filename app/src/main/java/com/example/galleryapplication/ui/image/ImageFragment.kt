package com.example.galleryapplication.ui.image

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.galleryapplication.databinding.FragmentImageBinding


class ImageFragment : Fragment() {

    val viewModel by lazy {
        ViewModelProvider(this).get(ImageViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        val binding = FragmentImageBinding.inflate(inflater)

        binding.viewModel = viewModel

        val imageRes = ImageFragmentArgs.fromBundle(requireArguments()).image

        Glide.with(requireContext()).load(imageRes).into(binding.imageView)


        return binding.root
    }


}