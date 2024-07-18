package com.adi121.statussaverupdate.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.adi121.statussaverupdate.databinding.DownloadsRvSingleItemBinding
import com.adi121.statussaverupdate.models.FileModel


class DownloadsAdapter(
    private val arrayList: ArrayList<FileModel>,
    private val onDownloadFileClicked: OnDownloadFileClicked
) : RecyclerView.Adapter<DownloadsAdapter.DataHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataHolder {
        val binding =
            DownloadsRvSingleItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DataHolder(binding)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: DataHolder, position: Int) {

        holder.binding.apply {
            Glide.with(ivDownload)
                .load(arrayList[position].uri)
                .into(ivDownload)

            ivVideo.isVisible = arrayList[position].uri.toString().endsWith(".mp4") ||
                    arrayList[position].uri.toString().endsWith(".gif")

            holder.itemView.setOnClickListener {
                onDownloadFileClicked.onDownloadedFileClicked(arrayList[position])
            }

            ivDelete.setOnClickListener {
                onDownloadFileClicked.deleteClicked(arrayList[position])
            }
        }


    }

    class DataHolder(val binding: DownloadsRvSingleItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface OnDownloadFileClicked {
        fun onDownloadedFileClicked(fileModel: FileModel)
        fun deleteClicked(fileModel: FileModel)
    }

}