package com.example.galleryapplication

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import java.util.*

object ImagesGallery {
    fun listOfImages(context: Context): ArrayList<String> {
        val cursor: Cursor?
        val index_data: Int
        val folder_name: Int
        val listOfAllImages = ArrayList<String>()
        var absolutePathOfImage: String

        val uri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME)

        val orderBy = MediaStore.Video.Media.DATE_TAKEN

        cursor = context.contentResolver.query(uri, projection, null, null, "$orderBy DESC")
        index_data = cursor!!.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)

        //folder name if needed
//          column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)

        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(index_data)
            listOfAllImages.add(absolutePathOfImage)
        }
        return listOfAllImages
    }
}