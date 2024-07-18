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
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.adi121.statussaverupdate.R
import com.adi121.statussaverupdate.databinding.ActivityDetailVideoBinding
import com.adi121.statussaverupdate.models.FileModel
import com.adi121.statussaverupdate.utils.Constants
import com.adi121.statussaverupdate.utils.Constants.KEY_POSITION_EXOPLAYER
import com.adi121.statussaverupdate.utils.SharedPrefs
import com.adi121.statussaverupdate.utils.checkFolder
import com.adi121.statussaverupdate.utils.showAlertDialog
import com.adi121.statussaverupdate.utils.showCustomToast
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.OutputStream

class DetailVideoActivity : AppCompatActivity() {

    lateinit var binding: ActivityDetailVideoBinding
    private var exoplayer: ExoPlayer? = null

    var fileModel: FileModel? = null
    private var currentTime: Long? = null

    private val sharedPrefs by lazy {
        SharedPrefs.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        savedInstanceState?.let {bundle->
            showCustomToast(bundle.getLong(KEY_POSITION_EXOPLAYER).toString())
            exoplayer?.seekTo(bundle.getLong(KEY_POSITION_EXOPLAYER))
            showCustomToast(bundle.getString("data").toString())

        }

        intent?.getParcelableExtra<FileModel>("itemData").also { videoData ->
            fileModel = videoData
            initExoplayer(videoData?.uri)
        }


        intent.getBooleanExtra("isFromSavedFragment", false).let {
            if(it){
                binding.fabAction.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_delete))
            }else{
                binding.fabAction.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_save))
            }
        }


        binding.fabAction.setOnClickListener {
            if(intent.getBooleanExtra("isFromSavedFragment", false)){
                deleteVideo()
            }else{
                saveVideo()
            }
        }


        binding.bottomAppBar.setOnMenuItemClickListener { item ->
            when (item?.itemId) {
                R.id.share -> {

                    if(intent.getBooleanExtra("isFromSavedFragment", false)){
                        shareVideoAfterSaving()
                    }else{
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                            shareVideBefore()
                        }else{
                            shareVideoAfterSaving()
                        }

                    }
                }

                R.id.back->{
                    handleBackPressed()
                }
            }
            true
        }

    }

    private fun initExoplayer(uri: Uri?) {
        //    Exoplayer initialization
        try {
            exoplayer=ExoPlayer.Builder(this).build()
            binding.exoplayerView.player=exoplayer

            val mediaItem= MediaItem.fromUri(uri!!)
            exoplayer?.setMediaItem(mediaItem)
            exoplayer?.prepare()
            exoplayer?.playWhenReady=true
            binding.exoplayerView.controllerShowTimeoutMs = 1000


        } catch (exception: Exception) {
            Log.e("TAG", "Error : $exception");
        }
    }


    private fun saveVideo(){
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
                    showCustomToast("Video Saved")


                } catch (exception: Exception) {

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
                showCustomToast("Video Saved")
            }
        } else {
            checkFolder()
            val file = File(intent?.getParcelableExtra<FileModel>("itemData")!!.path)
            val destinationPAth = Environment.getExternalStorageDirectory().absolutePath +
                    Constants.SAVED_FILES_LOCATION

            val destinationFile = File(destinationPAth)
            try {
                FileUtils.copyFileToDirectory(file, destinationFile)
                Toast.makeText(this, "Video Saved", Toast.LENGTH_SHORT).show()
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


    private fun shareVideBefore(){
        try {
            val sharingIntent = Intent(Intent.ACTION_SEND)
            sharingIntent.type = "video/*"
            sharingIntent.putExtra(Intent.EXTRA_STREAM, fileModel?.uri)
            startActivities(arrayOf(Intent.createChooser(sharingIntent, "Share with")))
        }catch (exception:Exception){

        }

    }


    private fun shareVideoAfterSaving(){
        val uri = FileProvider.getUriForFile(
            this, "${this.packageName}.provider",
            File(intent?.getParcelableExtra<FileModel>("itemData")!!.path)
        )
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.type = "video/*"
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        try {
            this.startActivity(Intent.createChooser(intent, "Share With"))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "No App Available", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteVideo(){
        try {
            showAlertDialog(
                this,
                "Are you sure you want to delete this file?",
                "Delete ?"
            ) {
                val baseDir = Environment.getExternalStorageDirectory().absolutePath
                val f = File("$baseDir/${Constants.SAVED_FILES_LOCATION}/${fileModel?.fileName}")
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

    override fun onDestroy() {
        super.onDestroy()
        exoplayer?.release()
    }

    override fun onPause() {
        super.onPause()
        exoplayer?.playWhenReady = false
        currentTime = exoplayer?.currentPosition
    }


    override fun onResume() {
        super.onResume()
        exoplayer?.playWhenReady = true
        currentTime?.let { exoplayer?.seekTo(it) }
    }


    private fun handleBackPressed() {
        finish()
    }



}