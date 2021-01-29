package com.example.galleryapplication.ui.main

import android.app.Application
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.util.*

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val _images = MutableLiveData<List<Image>>()

    val images: LiveData<List<Image>>
        get() = _images

    private val _navigateToSelectedImage = MutableLiveData<String>()

    val navigateToSelectedImage: LiveData<String>
        get() = _navigateToSelectedImage



    fun listImages(context: Context) {

        val listOfAllImages = ArrayList<Image>()

        val uri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.MediaColumns.DATA,
            MediaStore.MediaColumns.DISPLAY_NAME
        )

        val orderBy = MediaStore.Video.Media.DATE_TAKEN

        val cursor = context.contentResolver.query(uri, projection, null, null, "$orderBy DESC")
        val index_data = cursor!!.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
        val imageNameRaw = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)

        //folder name if needed
//         val column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)

        while (cursor.moveToNext()) {
            val absolutePathOfImage = cursor.getString(index_data)
            val imageName = cursor.getString(imageNameRaw)
            val image = Image(absolutePathOfImage, imageName)
            listOfAllImages.add(image)
        }

        _images.value = listOfAllImages

        cursor.close()

    }

    fun displayImage(path: String) {
        _navigateToSelectedImage.value = path
    }

    fun displayImageCompleted() {
        _navigateToSelectedImage.value = null
    }
}