package com.example.foto_2

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File


class MainActivity : AppCompatActivity() {
    var file : File? = null
    val REQUEST_PHOTO = 2

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
            val bitmap = BitmapFactory.decodeFile(file!!.path)
            imageView1.setImageBitmap(bitmap)
        }
    }
}
