package com.example.assignment3

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment3.databinding.EachItemBinding
import com.squareup.picasso.Picasso

class ImagesAdapter(private var mList: List<String>) :
    RecyclerView.Adapter<ImagesAdapter.ImagesViewHolder>() {
    //from here i have started
    interface OnItemClickListener {
        fun onItemClick(imagePath: String)
    }
    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.onItemClickListener = listener
    }
    //here i have created an inner class for holding the images which i have created the xml fike of eachitem

    inner class ImagesViewHolder(var binding: EachItemBinding) :
        RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagesViewHolder {
        val binding = EachItemBinding.inflate(LayoutInflater.from(parent.context) , parent , false)
        return ImagesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImagesViewHolder, position: Int) {
        //added a listiner for the itemview
        holder.itemView.setOnClickListener {
            val imagePath = mList[position]
            onItemClickListener?.onItemClick(imagePath)
        }
        //till here i have added
        with(holder.binding){
            with(mList[position]){
                Picasso.get()
                    .load(this)
                    .resize(122, 82)
                    .centerCrop()
                    .into(imageView)

                // Set the layout parameters for the imageView
                val layoutParams = imageView.layoutParams as ViewGroup.MarginLayoutParams
                val screenWidth = Resources.getSystem().displayMetrics.widthPixels
                val imageWidth = (screenWidth - (2 * layoutParams.marginStart)) / 2
                layoutParams.width = imageWidth
                layoutParams.height = (imageWidth * 82) / 122
                imageView.layoutParams = layoutParams
            }
        }
        val layoutParams = holder.itemView.layoutParams as ViewGroup.MarginLayoutParams
        if ((position + 1) % 4 == 0) {
            layoutParams.bottomMargin = 20
        } else {
            layoutParams.bottomMargin = 0
        }


    }

    override fun getItemCount(): Int {
        return mList.size
    }


}
