package com.example.galleryapplication.ui.image

import android.R.attr.name
import android.R.attr.path
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.galleryapplication.databinding.FragmentImageBinding
import com.example.galleryapplication.visible
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import kotlin.coroutines.CoroutineContext


class ImageFragment : Fragment() {

    private val viewModel by lazy {
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


        binding.buttonApplyNegative.setOnClickListener {
            binding.progressBar.visible(true)

            CoroutineScope(Dispatchers.IO).launch {
                val f = File(imageRes)
                val b = BitmapFactory.decodeStream(FileInputStream(f))
                val grayscaleBitmap = applyNegative(b, 1)

                launch(Dispatchers.Main) {
                    Glide.with(requireContext()).load(grayscaleBitmap).into(binding.imageView)
                    binding.progressBar.visible(false)

                }
            }
        }

        binding.buttonHistogram.setOnClickListener {
            findNavController().navigate(ImageFragmentDirections.actionImageFragmentToHistogramFragment())
        }

        binding.buttonApplyGrayscale.setOnClickListener{
            binding.progressBar.visible(true)

            CoroutineScope(Dispatchers.IO).launch {
                val f = File(imageRes)
                val b = BitmapFactory.decodeStream(FileInputStream(f))
                val sepiaBitmap = applyGrayscale(b)


                launch(Dispatchers.Main) {
                    binding.progressBar.visible(false)

                    Glide.with(requireContext()).load(sepiaBitmap).into(binding.imageView)
                }

            }
        }

        return binding.root
    }


    fun applyGrayscale(src: Bitmap): Bitmap? {
        // constant factors
        val GS_RED = 0.299
        val GS_GREEN = 0.587
        val GS_BLUE = 0.114

        // create output bitmap
        val bmOut = Bitmap.createBitmap(src.width, src.height, src.config)
        // pixel information
        var A: Int
        var R: Int
        var G: Int
        var B: Int
        var pixel: Int

        // get image size
        val width = src.width
        val height = src.height

        // scan through every single pixel
        for (x in 0 until width) {
            for (y in 0 until height) {
                // get one pixel color
                pixel = src.getPixel(x, y)
                // retrieve color of all channels
                A = Color.alpha(pixel)
                R = Color.red(pixel)
                G = Color.green(pixel)
                B = Color.blue(pixel)
                // take conversion up to one single value
                B = (GS_RED * R + GS_GREEN * G + GS_BLUE * B).toInt()
                G = B
                R = G
                // set new pixel color to output bitmap
                bmOut.setPixel(x, y, Color.argb(A, R, G, B))
            }
        }
        // return final image
        return bmOut
    }

    fun applyNegative(src: Bitmap, depth: Int): Bitmap? {
        // image size
        val width = src.width
        val height = src.height
        // create output bitmap
        val bmOut = Bitmap.createBitmap(width, height, src.config)
        // constant grayscale
        val GS_RED = 0.3
        val GS_GREEN = 0.59
        val GS_BLUE = 0.11
        // color information
        var A: Int
        var R: Int
        var G: Int
        var B: Int
        var pixel: Int

        // scan through all pixels
        for (x in 0 until width) {
            for (y in 0 until height) {
                // get pixel color
                pixel = src.getPixel(x, y)
                // get color on each channel
                A = Color.alpha(pixel)
                R = Color.red(pixel)
                G = Color.green(pixel)
                B = Color.blue(pixel)
                // apply grayscale sample
                R = (GS_RED * R + GS_GREEN * G + GS_BLUE * B).toInt()
                G = R
                B = G

                // apply intensity level for sepid-toning on each channel
                R = 255 - (depth * R)
                if (R > 255) {
                    R = 255
                }
                G = 255 -(depth * G)
                if (G > 255) {
                    G = 255
                }
                B = 255 -(depth * B)
                if (B > 255) {
                    B = 255
                }

                // set new pixel color to output image
                bmOut.setPixel(x, y, Color.argb(A, R, G, B))
            }
        }

        // return final image
        return bmOut
    }


}