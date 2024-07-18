package com.adi121.statussaverupdate.ui

import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment

import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.adi121.statussaverupdate.R
import com.adi121.statussaverupdate.databinding.ActivityDetailImageBinding
import com.adi121.statussaverupdate.models.FileModel
import com.adi121.statussaverupdate.utils.Constants.SAVED_FILES_LOCATION
import com.adi121.statussaverupdate.utils.SharedPrefs
import com.adi121.statussaverupdate.utils.checkFolder
import com.adi121.statussaverupdate.utils.showAlertDialog
import com.adi121.statussaverupdate.utils.showCustomToast
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.OutputStream

class DetailImageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailImageBinding

    var fileModel: FileModel? = null

    private val sharedPrefs by lazy {
        SharedPrefs.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailImageBinding.inflate(layoutInflater)
        setContentView(binding.root)


        fileModel = intent.getParcelableExtra<FileModel>("itemData")
        intent.getBooleanExtra("isFromSavedFragment", false).let {

            //val menu = binding.bottomAppBar.menu
            //menu.findItem(R.id.share).isVisible = it
            if (it) {
                binding.fabAction.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_delete))

            } else {
                binding.fabAction.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_save))
            }
        }

        binding.imageDetail.setImageURI(fileModel?.uri)

        binding.fabAction.setOnClickListener {
            if (intent.getBooleanExtra("isFromSavedFragment", false)) {
                deleteImage()
            } else {
                saveImage()
            }
        }


        binding.bottomAppBar.setOnMenuItemClickListener { item ->
            when (item?.itemId) {
                R.id.share -> {
                    if (intent.getBooleanExtra("isFromSavedFragment", false)) {
                        shareImageAfterSaving()
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            shareImageBefore()
                        } else {
                            shareImageAfterSaving()
                        }

                    }

                }
                R.id.back -> {
                    handleBackPressed()
                }
            }

            true
        }

    }

    private fun saveImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            if (fileModel?.fileName?.endsWith(".mp4")!! || fileModel?.fileName?.endsWith(".gif")!!) {
                val inputStream = contentResolver.openInputStream(Uri.parse(fileModel!!.path))
                val filename = "${System.currentTimeMillis()}.mp4"
                try {
                    val values = ContentValues()
                    values.put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    values.put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
                    values.put(
                        MediaStore.MediaColumns.RELATIVE_PATH,
                        Environment.DIRECTORY_DOWNLOADS + "/" + "StatusSaver"
                    )
                    val uri = contentResolver.insert(
                        MediaStore.Files.getContentUri("external"), values
                    )
                    val outputStream = uri?.let {
                        contentResolver.openOutputStream(it)
                    }
                    if (inputStream != null) {
                        outputStream?.write(inputStream.readBytes())
                    }
                    outputStream?.close()
                    Toast.makeText(this, "Image Saved", Toast.LENGTH_SHORT).show()

                } catch (exception: Exception) {
                    Log.d("TAG", "onCreate: ${exception.localizedMessage}")
                }

            } else {
                val bitmap = MediaStore.Images.Media.getBitmap(
                    this.contentResolver, Uri.parse(
                        fileModel!!.path
                    )
                )

                val filename = "${System.currentTimeMillis()}.jpg"
                var fos: OutputStream? = null
                contentResolver.also { resolver ->
                    val contentValues = ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                        put(
                            MediaStore.MediaColumns.RELATIVE_PATH,
                            Environment.DIRECTORY_DOWNLOADS + "/" + "StatusSaver"
                        )
                    }
                    val imageUri: Uri? = resolver.insert(
                        MediaStore.Files.getContentUri("external"),
                        contentValues
                    )
                    fos = imageUri?.let {
                        resolver.openOutputStream(it)
                    }
                }
                fos?.use {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                }
                fos?.flush()
                fos?.close()
                //Toast.makeText(this, "File Saved", Toast.LENGTH_SHORT).show()
                showCustomToast("Image Saved")
            }

        } else {
            checkFolder()
            val file = File(fileModel!!.path)
            val destinationPAth = Environment.getExternalStorageDirectory().absolutePath +
                    SAVED_FILES_LOCATION

            val destinationFile = File(destinationPAth)
            try {
                FileUtils.copyFileToDirectory(file, destinationFile)
                Toast.makeText(this, "Image Downloaded", Toast.LENGTH_SHORT).show()
                this.sendBroadcast(
                    Intent(
                        "android.intent.action.MEDIA_SCANNER_SCAN_FILE",
                        Uri.parse("file://" + Environment.getExternalStorageDirectory())
                    )
                )

            } catch (exception: Exception) {
                Log.d("TAG", "onDownloadCLick: ${exception.message}")
            }
        }
    }


    private fun deleteImage() {
        try {
            showAlertDialog(
                this,
                "Are you sure you want to delete this file?",
                "Delete ?"
            ) {
                val baseDir = Environment.getExternalStorageDirectory().absolutePath
                val f = File("$baseDir/${SAVED_FILES_LOCATION}/${fileModel?.fileName}")
                val isDeleted = f.delete()
                if (isDeleted) {
                    showCustomToast("File Deleted")
                    finish()
                } else {
                    showCustomToast("Something went wrong , try again later")
                }
            }.show()

        } catch (exception: Exception) {
            showCustomToast("Something went wrong , try again later")
            Log.d("TAG", "deleteClickederror: ${exception.localizedMessage}")
        }

    }


    private fun shareImageBefore() {
        try {
            val sharingIntent = Intent(Intent.ACTION_SEND)
            sharingIntent.type = "image/*"
            sharingIntent.putExtra(Intent.EXTRA_STREAM, fileModel?.uri)
            startActivities(arrayOf(Intent.createChooser(sharingIntent, "Share with")))

        } catch (exception: Exception) {

        }

    }


    private fun shareImageAfterSaving() {
        val uri = FileProvider.getUriForFile(
            this, "${this.packageName}.provider",
            File(fileModel!!.path)
        )
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        try {
            this.startActivity(Intent.createChooser(intent, "Share With"))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "No App Available", Toast.LENGTH_SHORT).show()
        }

    }


    private fun handleBackPressed() {
        finish()

        // uncomment this later

//        if(isActionPerformed){
//            if(!sharedPrefs?.isAppRated!!&& sharedPrefs?.appOpenedCounter!!%5==0){
//                showAlertDialogWithNegativeCall(this,"Liking the app ? Take a moment to rate us , It will means a lot to us , Thanks for your support :)","Rate this app ",{
//                    val intent = Intent(Intent.ACTION_VIEW)
//                    intent.data = Uri.parse("market://details?id=com.vickyneji.statussaver")
//                    startActivity(intent)
//                    sharedPrefs?.isAppRated = true
//                },{
//                    sharedPrefs?.appOpenedCounter=sharedPrefs?.appOpenedCounter!!+1
//                    super.onBackPressed()
//                }).show()
//            }else{
//                super.onBackPressed()
//            }
//        }else{
//            super.onBackPressed()
//        }
//
//
//        //uncomment this later
//
//        if (isActionPerformed) {
//            if (sharedPrefs?.lastAdShownTime!! + 120000L < System.currentTimeMillis()) {
//                if (mInterstitialAd != null) {
//                    mInterstitialAd?.show(this)
//                    sharedPrefs?.lastAdShownTime = System.currentTimeMillis()
//                    super.onBackPressed()
//                } else {
//                    super.onBackPressed()
//                }
//
//            } else {
//                super.onBackPressed()
//            }
//
//        } else {
//            super.onBackPressed()
//        }
    }


}