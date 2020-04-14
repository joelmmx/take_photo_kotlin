package com.example.foto_2

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File


class MainActivity : AppCompatActivity() {
    var file : File? = null
    val REQUEST_PHOTO = 2
    var width  : Int = 0
    var height : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val captureImage = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        val canTakePhoto = captureImage.resolveActivity(packageManager) != null
        button1.setEnabled(canTakePhoto)
        button1.setOnClickListener{
            file = getPhotoFile()
            val uri: Uri = FileProvider.getUriForFile(applicationContext, "com.example.foto_2.filrprovider", file!!)
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri)

            val camaraActivities: List<ResolveInfo> = applicationContext.getPackageManager().queryIntentActivities(captureImage, PackageManager.MATCH_DEFAULT_ONLY)

            for (activity in camaraActivities) {
                applicationContext.grantUriPermission(activity.activityInfo.packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            }

            startActivityForResult(captureImage, REQUEST_PHOTO)
        }

    }

    fun getPhotoFile():File{
        val fileDir = applicationContext.filesDir
        return File(fileDir , "IMG_photo.jpg")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode != Activity.RESULT_OK){
            return
        }
        if (requestCode == REQUEST_PHOTO) {
            val uri = FileProvider.getUriForFile(applicationContext, "com.example.foto_2.filrprovider", file!!)
            applicationContext.revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            imageView1.viewTreeObserver.addOnGlobalLayoutListener {
                width = imageView1.width
                height = imageView1.height
                imageView1.setImageBitmap(getScaleBitmap(file!!.path , width , height))
            }
            if(width!=0&&height!=0){
                imageView1.setImageBitmap(getScaleBitmap(file!!.path , width , height))
            }else{
                val size = Point()
                this.windowManager.defaultDisplay.getSize(size)
                imageView1.setImageBitmap(getScaleBitmap(file!!.path , size.x , size.y))
            }

        }
    }

    fun getScaleBitmap(path: String?, desWidth: Int, destHeight: Int): Bitmap? {
        //Read int the dimensions of the image on disk
        var options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(path, options)
        val srcWidth = options.outWidth.toFloat()
        val srcHeight = options.outHeight.toFloat()

        //Figure out how much to scale down by
        var inSampleSize = 1
        if (srcHeight > destHeight || srcWidth > desWidth) {
            val heightScale = srcHeight / destHeight
            val widthScale = srcWidth / desWidth
            inSampleSize =
                Math.round(if (heightScale > widthScale) heightScale else widthScale)
        }
        options = BitmapFactory.Options()
        options.inSampleSize = inSampleSize

        //Read in and create final bitmap
        return BitmapFactory.decodeFile(path, options)
    }
}
