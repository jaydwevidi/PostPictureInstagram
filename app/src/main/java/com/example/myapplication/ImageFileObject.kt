package com.example.myapplication

import android.graphics.Bitmap
import java.io.File

data class ImageFileObject(
    val title : String ,
    val path : String ,
    //val bitmap: Bitmap,
    val file : File
)
