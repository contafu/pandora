package com.android.pandora.display

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.android.pandora.Clay
import com.android.pandora.R
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import java.io.File
import java.util.*

/**
 * Created by tangchao on 2017/8/8.
 */
internal class ClaysAdapter(val context: Context, private val arrayList: MutableList<Clay>, private val block: ((Clay) -> Unit)) : RecyclerView.Adapter<ClaysAdapter.Holder>() {

    override fun getItemCount(): Int = arrayList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.lib_pandora_adapter_album_item, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val `var` = arrayList[position]
        val view = holder.imageView
        if (null != view) {
            Glide.with(context)
                    .load(File(`var`.data))
                    .apply(RequestOptions().centerInside())
                    .thumbnail(0.1f)
                    .into(view)
        }

        holder.name?.text = `var`.name
        holder.count?.text = String.format(Locale.CHINA, context.resources.getString(R.string.lib_pandora_number), `var`.count)
        if (`var`.check) {
            holder.checkBox?.visibility = View.VISIBLE
        } else {
            holder.checkBox?.visibility = View.INVISIBLE
        }

        holder.itemView?.setOnClickListener {
            block.invoke(arrayList[position])
            if (!`var`.check) {
                `var`.check = true
                highlightItem(position)
            }
        }
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView = itemView.findViewById<ImageView?>(R.id.lib_pandora_adapter_album_image_view)
        var checkBox = itemView.findViewById<ImageView?>(R.id.lib_pandora_adapter_album_check_box)
        var name = itemView.findViewById<TextView?>(R.id.lib_pandora_adapter_album_name)
        var count = itemView.findViewById<TextView?>(R.id.lib_pandora_adapter_album_count)
    }

    fun update(arrayList: MutableList<Clay>) {
        this.arrayList.clear()
        this.arrayList.addAll(arrayList)
        notifyDataSetChanged()
    }

    /**
     * 高亮指定条目
     */
    private fun highlightItem(position: Int) {
        arrayList.forEach {
            it.check = false
        }

        arrayList[position].check = true

        notifyDataSetChanged()
    }
}