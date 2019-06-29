package com.android.pandora.pupil

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

/**
 * Created by tangchao on 2017/8/5.
 * 图片预览
 */
internal class PupilViewPagerAdapter(fm: FragmentManager, private val arrayList: List<Fragment>) : FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment = arrayList[position]

    override fun getCount(): Int = arrayList.size

}