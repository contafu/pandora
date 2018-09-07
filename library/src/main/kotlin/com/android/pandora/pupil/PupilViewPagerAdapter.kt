package com.android.pandora.pupil

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter

/**
 * Created by tangchao on 2017/8/5.
 * 图片预览
 */
internal class PupilViewPagerAdapter(fm: FragmentManager, private val arrayList: List<Fragment>) : FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment = arrayList[position]

    override fun getCount(): Int = arrayList.size

}