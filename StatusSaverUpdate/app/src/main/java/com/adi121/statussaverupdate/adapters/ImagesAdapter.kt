package com.adi121.statussaverupdate.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.adi121.statussaverupdate.databinding.ImagesRvSingleItemBinding
import com.adi121.statussaverupdate.models.FileModel

class ImagesAdapter(
    private val imageArray: ArrayList<FileModel>,
    private val onDownloadClicked: OnDownloadClicked
) : RecyclerView.Adapter<ImagesAdapter.ImageDataHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageDataHolder {
        val binding =
            ImagesRvSingleItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        //val view= LayoutInflater.from(parent.context).inflate(R.layout.images_rv_single_item,parent,false)
        return ImageDataHolder(binding)
    }

    override fun getItemCount(): Int {
        return imageArray.size
    }

    override fun onBindViewHolder(holder: ImageDataHolder, position: Int) {
        holder.bindData(imageArray[position])


        holder.itemView.setOnClickListener {
            onDownloadClicked.onItemClicked(imageArray[position])
        }
    }

    inner class ImageDataHolder(val binding: ImagesRvSingleItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(fileModel: FileModel) {
            binding.apply {
                Glide.with(ivImage)
                    .load(fileModel.uri)
                    .into(ivImage)

                ivVideoIcon.isVisible =
                    fileModel.uri.toString().endsWith(".mp4") || fileModel.uri.toString()
                        .endsWith(".gif")
            }

        }


    }

    interface OnDownloadClicked {

        fun onItemClicked(fileModel: FileModel)
    }

}