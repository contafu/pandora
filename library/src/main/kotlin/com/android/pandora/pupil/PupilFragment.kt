package com.android.pandora.pupil

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.pandora.Clay
import com.android.pandora.pandora.R
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.lib_pandora_fragment_pupil.*
import java.io.File

/**
 * Created by tangchao on 2017/8/5.
 */
internal class PupilFragment : Fragment() {

    companion object {
        @JvmStatic
        fun getInstance(): PupilFragment = PupilFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.lib_pandora_fragment_pupil, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val uri = arguments?.getParcelable<Clay>("uri")
        Glide.with(this)
                .load(File(uri?.data))
                .apply(RequestOptions().fitCenter())
                .thumbnail(0.1f)
                .into(lib_pandora_fragment_pupil_image_view)
    }
}