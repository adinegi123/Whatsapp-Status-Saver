package com.adi121.statussaverupdate.utils

import android.app.AlertDialog
import android.content.Context
import android.os.Environment
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import com.adi121.statussaverupdate.R
import java.io.File

 fun checkFolder(){
    val path= Environment.getExternalStorageDirectory().absolutePath+ Constants.SAVED_FILES_LOCATION
    val dir= File(path)
    var  isDirectoryCreated = dir.exists()
    // Log.d(TAG, "com.vickyneji.statussaver.utils.checkFolder: $isDirectoryCreated")
    if(!isDirectoryCreated){
        isDirectoryCreated=dir.mkdir()
    }

}

fun Context.showCustomToast(message:String){
    val view= LayoutInflater.from(this)
        .inflate(R.layout.toast_layout,null)

    val toast= Toast(this)
    val tvMessage=view.findViewById<TextView>(R.id.tvMessage)
        .also {
            it.text=message
        }

    toast.view=view
    toast.show()
}

inline fun showAlertDialog(context:Context, message: String, title:String, crossinline onClick:()->Unit): AlertDialog {
    return AlertDialog.Builder(context)
        .setMessage(message)
        .setTitle(title)
        .setPositiveButton("Ok"){dialog,which->
            onClick()
        }.setNegativeButton("No"){
                dialog,_->
            dialog.dismiss()
        }.create()

}


inline fun showAlertDialogWithNegativeCall(context:Context, message: String, title:String, crossinline onClick:()->Unit,crossinline onNoClick:()->Unit):AlertDialog{
    return AlertDialog.Builder(context)
        .setMessage(message)
        .setTitle(title)
        .setCancelable(false)
        .setPositiveButton("Rate Now"){dialog,which->
            onClick()
        }.setNegativeButton("Later"){
                dialog,_->
            onNoClick()
            dialog.dismiss()
        }.create()

}



