package com.adi121.statussaverupdate.ui.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import com.adi121.statussaverupdate.adapters.DownloadsAdapter
import com.adi121.statussaverupdate.databinding.FragmentDownloadsBinding
import com.adi121.statussaverupdate.models.FileModel
import com.adi121.statussaverupdate.ui.DetailImageActivity
import com.adi121.statussaverupdate.ui.DetailVideoActivity
import com.adi121.statussaverupdate.utils.Constants
import com.adi121.statussaverupdate.utils.Constants.SAVED_FILES_LOCATION
import com.adi121.statussaverupdate.utils.showAlertDialog
import com.adi121.statussaverupdate.utils.showCustomToast
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.io.File

class DownloadsFragment : Fragment(),
    EasyPermissions.PermissionCallbacks,
    DownloadsAdapter.OnDownloadFileClicked {

    private lateinit var binding: FragmentDownloadsBinding

    private val perms = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDownloadsBinding.inflate(inflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getDownloadsData()
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (EasyPermissions.hasPermissions(requireContext(), *perms)) {
                getDownloadsData()
            } else {
                requestPermissionFromUser()
            }

        } else {
            getDownloadsData()

        }

        binding.refreshLayoutSaved.setOnRefreshListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (EasyPermissions.hasPermissions(requireContext(), *perms)) {
                    getDownloadsData()
                    binding.refreshLayoutSaved.isRefreshing = false
                } else {
                    requestPermissionFromUser()
                }

            } else {
                getDownloadsData()
                binding.refreshLayoutSaved.isRefreshing = false
            }
        }

    }


    private fun getDownloadsData() {

        val imageData = ArrayList<FileModel>()
        val targetPath =
            Environment.getExternalStorageDirectory().absolutePath + Constants.SAVED_FILES_LOCATION
        val fileDirectory = File(targetPath)
        val files = fileDirectory.listFiles()?.reversed()

        if (!files.isNullOrEmpty()) {
            binding.tvNoDownloads.isVisible = false
            for (file in files) {
                imageData.add(FileModel(file.absolutePath, file.name, Uri.fromFile(file)))
            }

            binding.rvDownloads.adapter = DownloadsAdapter(imageData, this)


        } else {
            binding.rvDownloads.isVisible = false
            binding.tvNoDownloads.isVisible = true
        }

    }

    private fun requestPermissionFromUser() {
        EasyPermissions.requestPermissions(
            this, "You need to allow these permissions to access this app features",
            Constants.REQUEST_PERMISSIONS, *perms
        )

    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            requestPermissionFromUser()
        }

    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        getDownloadsData()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.REQUEST_PERMISSIONS) {
            EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
        }
    }

    override fun onDownloadedFileClicked(fileModel: FileModel) {
        if (fileModel.uri.toString().endsWith(".mp4") || fileModel.uri.toString()
                .endsWith(".gif")
        ) {
            val intent = Intent(requireContext(), DetailVideoActivity::class.java).apply {
                putExtra("itemData", fileModel)
                putExtra("isFromSavedFragment", true)
                startActivity(this)
            }
        } else {
            val intent = Intent(requireContext(), DetailImageActivity::class.java).apply {
                putExtra("itemData", fileModel)
                putExtra("isFromSavedFragment", true)
                startActivity(this)
            }

        }

    }

    override fun deleteClicked(fileModel: FileModel) {

        deleteFile(fileModel)
    }

    private fun deleteFile(fileModel: FileModel) {
        try {
            showAlertDialog(
                requireContext(),
                "Are you sure you want to delete this file?",
                "Delete ?"
            ) {
                val baseDir = Environment.getExternalStorageDirectory().absolutePath
                val f = File("$baseDir/${SAVED_FILES_LOCATION}/${fileModel.fileName}")
                val isDeleted = f.delete()
                if (isDeleted) {
                    requireContext().showCustomToast("File Deleted")
                    getDownloadsData()

                } else {
                    requireContext().showCustomToast("Something went wrong , try again later")
                }
            }.show()

        } catch (exception: Exception) {
            requireContext().showCustomToast("Something went wrong , try again later")
            Log.d("TAG", "deleteClickederror: ${exception.localizedMessage}")
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.REQUEST_PERMISSIONS) {
                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.R) {
                    if (Environment.isExternalStorageManager()) {
                        Toast.makeText(requireContext(), "Permission granted", Toast.LENGTH_SHORT)
                            .show()
                        getDownloadsData()
                    }

                }
            }
        }

    }

    override fun onResume() {
        super.onResume()
        getDownloadsData()
    }


}