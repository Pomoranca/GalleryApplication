package com.example.galleryapplication.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.galleryapplication.databinding.GalleryItemBinding

class GalleryAdapter(
       private val context: Context,
       private val images: List<String>,
       private val onClickListener: OnClickListener
) : RecyclerView.Adapter<GalleryAdapter.ImageViewHolder>() {

    class ImageViewHolder(val binding: GalleryItemBinding) : RecyclerView.ViewHolder(binding.root) {

    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = GalleryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val image = images[position]
        Glide.with(context)
                .load(image)
                .into(holder.binding.image)

        holder.itemView.setOnClickListener{
            onClickListener.clickListener(image)
        }
    }

    override fun getItemCount(): Int {
       return images.size
    }

    class OnClickListener(val clickListener: (path: String) -> Unit) {
        fun onClick(path: String) = clickListener(path)
    }
}