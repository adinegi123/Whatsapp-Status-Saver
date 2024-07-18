package com.adi121.statussaverupdate.models
import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class  FileModel(val path:String, val fileName:String, val uri:Uri):Parcelable {

}