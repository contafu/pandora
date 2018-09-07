package com.android.pandora.display

import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckedTextView
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import com.android.pandora.Clay
import com.android.pandora.pandora.R
import com.android.pandora.countInLine
import com.android.pandora.pupil.PupilActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import java.io.File

/**
 * Created by tangchao on 2017/8/8.
 */
internal class ClayAdapter(val context: Context, private val arrayList: MutableList<Clay>, private val max: Int = 9,
                           private val removeBlock: ((Clay) -> Unit), private val addBlock: ((Clay) -> Unit), private val countBlock: ((Int) -> Unit)) : RecyclerView.Adapter<ClayAdapter.Holder>() {

    /**
     * 蒙层颜色
     */
    private val colorFilter = 0x70000000
    private val colorFilterClear = 0x00000000

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val `var`: Clay = arrayList[position]
        val view = holder.imageView
        if (null != view) {
            Glide.with(context)
                    .load(File(`var`.data))
                    .apply(RequestOptions().apply {
                        fitCenter()
                        placeholder(R.color.lib_pandora_817F7F)
                    })
                    .thumbnail(0.1f)
                    .into(view)
        }

        holder.checkedTextView?.isChecked = `var`.check
        if (`var`.check) {
            holder.imageView?.setColorFilter(colorFilter, PorterDuff.Mode.SRC_OVER)
        } else {
            holder.imageView?.setColorFilter(colorFilterClear, PorterDuff.Mode.SRC_OVER)
        }

        holder.checkedTextView?.setOnClickListener { v ->
            if ((v as CheckedTextView).isChecked) {
                `var`.check = false
                v.toggle()
                removeBlock.invoke(`var`)
                holder.imageView?.setColorFilter(colorFilterClear, PorterDuff.Mode.SRC_OVER)
            } else {
                if (max > calculatorCount()) {
                    `var`.check = true
                    v.toggle()
                    addBlock.invoke(`var`)
                    holder.imageView?.setColorFilter(colorFilter, PorterDuff.Mode.SRC_OVER)
                } else {
                    Toast.makeText(context.applicationContext, String.format(context.resources.getString(R.string.lib_pandora_check_max_count), max), Toast.LENGTH_SHORT).show()
                }
            }
            countBlock.invoke(calculatorCount())
        }

        holder.imageView?.setOnClickListener {
            context.startActivity(Intent(context, PupilActivity::class.java).apply {
                putExtras(Bundle().apply {
                    putInt("position", position)
                    putParcelableArray("arrayList", arrayList.toTypedArray())
                })
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.lib_pandora_adapter_photo_item, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int = arrayList.size

    inner class Holder(val view: View) : RecyclerView.ViewHolder(view) {
        var imageView = view.findViewById<ImageView?>(R.id.lib_pandora_photo_adapter_image_view)
        var checkedTextView = view.findViewById<CheckedTextView?>(R.id.lib_pandora_photo_adapter_checked_text_view)

        init {
            val width: Int = context.resources.displayMetrics.widthPixels
            imageView?.layoutParams = FrameLayout.LayoutParams(
                    width / countInLine,
                    width / countInLine
            )
        }
    }

    fun update(arrayList: MutableList<Clay>?) {
        if (null != arrayList) {
            this.arrayList.clear()
            this.arrayList.addAll(arrayList)
            notifyDataSetChanged()
        }
    }

    /**
     * 实时计算当前选中的个数
     */
    private fun calculatorCount(): Int {
        var count = 0
        arrayList.filter { it.check }
                .forEach { count++ }
        return count
    }
}