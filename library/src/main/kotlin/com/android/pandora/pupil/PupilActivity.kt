package com.android.pandora.pupil

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.android.pandora.R
import kotlinx.android.synthetic.main.lib_pandora_activity_pupil.*
import java.util.*

/**
 * 预览页面
 */
internal class PupilActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.lib_pandora_activity_pupil)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val lp: WindowManager.LayoutParams = window.attributes
            lp.flags = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or lp.flags
        }

        lib_pandora_tool_bar_navigation_button.setOnClickListener {
            onBackPressed()
        }

        val bundle: Bundle? = intent.extras

        val position: Int = bundle?.getInt("position") ?: 0
        val arrayList = bundle?.getParcelableArray("arrayList")

        val dataList: MutableList<Fragment> = mutableListOf()
        arrayList?.forEach {
            dataList.add(PupilFragment.getInstance().apply {
                arguments = Bundle().apply {
                    putParcelable("uri", it)
                }
            })
        }

        lib_pandora_pupil_view_pager.adapter = PupilViewPagerAdapter(supportFragmentManager, dataList)
        if (position >= arrayList?.size ?: 0) lib_pandora_pupil_view_pager.currentItem = 0 else lib_pandora_pupil_view_pager.currentItem = position

        lib_pandora_pupil_view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(p: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(p: Int) {
                lib_pandora_tool_bar_title_view.text = String.format(Locale.CHINA, resources.getString(R.string.lib_pandora_pupil_indicator), p + 1, arrayList?.size.toString())
            }
        })

        if (position >= arrayList?.size ?: 0) {
            lib_pandora_tool_bar_title_view.text = String.format(Locale.CHINA, resources.getString(R.string.lib_pandora_pupil_indicator), 1, arrayList?.size.toString())
        } else {
            lib_pandora_tool_bar_title_view.text = String.format(Locale.CHINA, resources.getString(R.string.lib_pandora_pupil_indicator), position + 1, arrayList?.size.toString())
        }
    }
}