package com.gzeinnumer.externaltakefotofromcamerakt

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.gzeinnumer.externaltakefotofromcamerakt.helper.FunctionGlobalDir
import com.gzeinnumer.externaltakefotofromcamerakt.helper.imagePicker.FileCompressor
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity_"

    val REQUEST_TAKE_PHOTO = 1
    lateinit var mPhotoFile: File
    lateinit var mCompressor: FileCompressor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = TAG

        mCompressor = FileCompressor(this)
        mCompressor.setDestinationDirectoryPath(FunctionGlobalDir.getStorageCard + FunctionGlobalDir.appFolder)

        btn_camera.setOnClickListener { dispatchTakePictureIntent() }
    }


    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
            if (photoFile != null) {
                val photoURI = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID.toString() + ".provider", photoFile)
                mPhotoFile = photoFile
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
            }
        }
    }

    //simpan data di dalam root folder sebagai temporary
    @Throws(IOException::class)
    private fun createImageFile(): File? {
        val timeStamp = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
        val mFileName = "JPEG_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_DCIM)
        return File.createTempFile(mFileName, ".jpg", storageDir)
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_TAKE_PHOTO) {
                try {
                    mPhotoFile = mCompressor.compressToFile(mPhotoFile)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                Glide.with(this@MainActivity).load(mPhotoFile).into(img)
                Log.d(TAG, "onActivityResult: $mPhotoFile")
                Toast.makeText(this, "Image Path : $mPhotoFile", Toast.LENGTH_SHORT).show()
            }
        }
    }
}