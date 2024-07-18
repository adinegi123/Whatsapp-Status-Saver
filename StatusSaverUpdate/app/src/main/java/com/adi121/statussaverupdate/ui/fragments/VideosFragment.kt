package com.adi121.statussaverupdate.ui.fragments

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import com.lazygeniouz.dfc.file.DocumentFileCompat
import com.adi121.statussaverupdate.adapters.ImagesAdapter
import com.adi121.statussaverupdate.databinding.FragmentVideosBinding
import com.adi121.statussaverupdate.models.FileModel
import com.adi121.statussaverupdate.ui.DetailImageActivity
import com.adi121.statussaverupdate.ui.DetailVideoActivity
import com.adi121.statussaverupdate.utils.Constants.REQUEST_PERMISSIONS
import com.adi121.statussaverupdate.utils.Constants.TARGET_PATH
import com.adi121.statussaverupdate.utils.Constants.TARGET_PATH_BUSINESS
import com.adi121.statussaverupdate.utils.Constants.TARGET_PATH_SECOND
import com.adi121.statussaverupdate.utils.SharedPrefs
import com.adi121.statussaverupdate.utils.showCustomToast
import com.vickyneji.statussaver.dialog.BottomSheetHowTo
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.io.File

class VideosFragment : Fragment(), EasyPermissions.PermissionCallbacks,
    ImagesAdapter.OnDownloadClicked {

    private lateinit var binding: FragmentVideosBinding


    private val perms = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    // shared-preference
    private val sharedPrefs by lazy {
        SharedPrefs.getInstance(requireContext())
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentVideosBinding.inflate(inflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkVerAndGetData()

        if (sharedPrefs?.isWhatsAppMode!!) {
            binding.tvChangeMode.text = "Change to WhatsApp Business"
        } else {
            binding.tvChangeMode.text = "Change to WhatsApp"
        }




        binding.tvChangeMode.setOnClickListener {
            val currentModeValue = sharedPrefs?.isWhatsAppMode
            if (currentModeValue == true) {
                binding.tvChangeMode.text = "Change to WhatsApp"
                requireContext().showCustomToast("Changed to WhatsApp Business")

            } else {
                binding.tvChangeMode.text = "Change to WhatsApp Business"
                requireContext().showCustomToast("Changed to WhatsApp")
            }
            sharedPrefs?.isWhatsAppMode = !currentModeValue!!
            startActivity(Intent.makeRestartActivityTask(activity?.intent?.component))
        }

        binding.btnGrantPermission.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                getFolderPermissions()
            }
        }

        binding.refreshLayout.setOnRefreshListener {
            checkVerAndGetData()
            binding.refreshLayout.isRefreshing = false
        }

        binding.ivInfo.setOnClickListener {
            val bottomSheetDialogFragment = BottomSheetHowTo()
            bottomSheetDialogFragment.show(childFragmentManager, "ModalBottomSheet")
        }


    }


    private fun checkVerAndGetData(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            if (sharedPrefs?.isWhatsAppMode!!) {
                if (File(Environment.getExternalStorageDirectory().absolutePath + TARGET_PATH).exists()) {
                    if (sharedPrefs?.uri!!.isEmpty()) {
                        binding.linearGrantPermission.isVisible = true
                    } else {
                        binding.linearGrantPermission.isVisible = false
                        getAndroidRData()
                    }
                } else {
                    binding.linearNoStatus.isVisible = true
                }
            } else {
                if (File(Environment.getExternalStorageDirectory().absolutePath + TARGET_PATH_BUSINESS).exists()) {
                    if (sharedPrefs?.businessUri!!.isEmpty()) {
                        binding.linearGrantPermission.isVisible = true

                    } else {
                        binding.linearGrantPermission.isVisible = false
                        getAndroidRData()
                    }
                } else {
                    binding.linearNoStatus.isVisible = true
                }
            }

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (EasyPermissions.hasPermissions(requireContext(), *perms)) {
                getOtherVersionData()
            } else {
                requestPermissionFromUser()
            }

        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            getOtherVersionData()

        }
    }


    private fun getOtherVersionData() {
        val imageData = ArrayList<FileModel>()
        var targetPath: String? = null
        if (sharedPrefs?.isWhatsAppMode!!) {
            if (File(Environment.getExternalStorageDirectory().absolutePath + TARGET_PATH).exists()) {
                targetPath = Environment.getExternalStorageDirectory().absolutePath + TARGET_PATH
            } else if (File(Environment.getExternalStorageDirectory().absolutePath + TARGET_PATH_SECOND).exists()) {
                targetPath =
                    Environment.getExternalStorageDirectory().absolutePath + TARGET_PATH_SECOND
            } else {
                binding.linearNoStatus.isVisible = true
                return
            }
        } else {
            if (File(Environment.getExternalStorageDirectory().absolutePath + TARGET_PATH_BUSINESS).exists()) {
                targetPath =
                    Environment.getExternalStorageDirectory().absolutePath + TARGET_PATH_BUSINESS
            } else {
                binding.linearNoStatus.isVisible = true
                return
            }
        }

        targetPath.let {
            val fileDirectory = File(targetPath)
            val files = fileDirectory.listFiles()?.reversed()
            if (!files.isNullOrEmpty() && files.size > 1) {
                binding.linearNoStatus.isVisible = false
                //btnGoToWhatsapp.isVisible = false
                for (file in files) {

                    if (
                        Uri.fromFile(file).toString().endsWith(".mp4") || Uri.fromFile(file)
                            .toString().endsWith(".gif")
                    ) {
                        imageData.add(FileModel(file.absolutePath, file.name, Uri.fromFile(file)))
                    }
                }

                binding.rvImages.adapter = ImagesAdapter(imageData, this)
            } else {
                binding.tvNoStatus.text="No video status found \n View any status in Whatsapp and come back here"
                binding.linearNoStatus.isVisible = true
                //btnGoToWhatsapp.isVisible = true
            }
        }
    }


    private fun requestPermissionFromUser() {
        EasyPermissions.requestPermissions(
            this, "You need to allow these permissions to access this app features",
            REQUEST_PERMISSIONS, *perms
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
        getOtherVersionData()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSIONS) {
            EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
        }
    }


    override fun onItemClicked(fileModel: FileModel) {
        if (fileModel.uri.toString().endsWith(".mp4") || fileModel.uri.toString()
                .endsWith(".gif")
        ) {
            val intent = Intent(requireContext(), DetailVideoActivity::class.java).apply {
                putExtra("itemData", fileModel)
                startActivity(this)
            }
        } else {
            val intent = Intent(requireContext(), DetailImageActivity::class.java).apply {
                putExtra("itemData", fileModel)
                startActivity(this)
            }

        }
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getFolderPermissions() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        val statusUri = if (sharedPrefs?.isWhatsAppMode!!)
            Uri.parse("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fmedia/document/primary%3AAndroid%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia%2F.Statuses")
        else Uri.parse("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fmedia/document/primary%3AAndroid%2Fmedia%2Fcom.whatsapp.w4b%2FWhatsApp%20Business%2FMedia%2F.Statuses")
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, statusUri)
        startActivityForResult(intent, 1001)

    }


    private fun getAndroidRData() {

        val uri = if(sharedPrefs?.isWhatsAppMode!!) sharedPrefs?.uri else sharedPrefs?.businessUri
        uri?.let {
            requireContext().contentResolver.takePersistableUriPermission(
                Uri.parse(it),
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            val fileDoc = DocumentFileCompat.fromTreeUri(requireContext(), Uri.parse(it))
            if (fileDoc != null) {
                val statusList = arrayListOf<FileModel>()
                for (file: DocumentFileCompat in fileDoc.listFiles()) {
                    if (!file.name.endsWith(".nomedia")) {
                        if(file.name.endsWith(".mp4")
                            || file.name.endsWith(".gif")
                        ){
                            val modelClass =
                                FileModel(file.uri.toString(), file.name, file.uri)
                            statusList.add(modelClass)
                        }
                    }
                }
                if(statusList.isNotEmpty()){
                    binding.rvImages.adapter = ImagesAdapter(statusList, this)
                }else{
                    binding.tvNoStatus.text="No video status found . \n View any status in WhatsApp and come back"
                    binding.linearNoStatus.isVisible=true
                }


            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {

            val treeUri = data?.data
            // Save to shared pref
            if (sharedPrefs?.isWhatsAppMode!!) {
                sharedPrefs?.uri = treeUri.toString()
            } else {
                sharedPrefs?.businessUri = treeUri.toString()
            }

            treeUri?.let {
                requireContext().contentResolver.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                val fileDoc = DocumentFileCompat.fromTreeUri(requireContext(), it)
                if (fileDoc != null) {
                    val statusList = arrayListOf<FileModel>()
                    // replace documentfile with documentfilecompat
                    for (file: DocumentFileCompat in fileDoc.listFiles()) {
                        if (!file.name.endsWith(".nomedia")) {
                            if(file.name.endsWith(".mp4")
                                || file.name.endsWith(".gif")
                            ){
                                val modelClass =
                                    FileModel(file.uri.toString(), file.name, file.uri)
                                statusList.add(modelClass)
                            }
                        }
                    }
                    if (!statusList.isNullOrEmpty()) {
                        binding.rvImages.adapter = ImagesAdapter(statusList, this)
                    } else {
                        binding.linearNoStatus.isVisible = true
                    }

                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            if (sharedPrefs?.isWhatsAppMode!!) {
                if (File(Environment.getExternalStorageDirectory().absolutePath + TARGET_PATH).exists()) {
                    binding.linearGrantPermission.isVisible = sharedPrefs?.uri!!.isEmpty()
                } else {
                    binding.linearNoStatus.isVisible = true
                }
            } else {
                if (File(Environment.getExternalStorageDirectory().absolutePath + TARGET_PATH_BUSINESS).exists()) {
                    binding.linearGrantPermission.isVisible = sharedPrefs?.businessUri!!.isEmpty()
                } else {
                    binding.linearNoStatus.isVisible = true
                }
            }
        }

    }


}