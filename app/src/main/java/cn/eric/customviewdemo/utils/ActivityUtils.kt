package cn.eric.customviewdemo.utils

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager

/**
 * Created by hyx on 2017/6/19.
 */

object ActivityUtils {
    fun addFragmentToActivity(fragmentManager: FragmentManager,
                              fragment: Fragment?, frameId: Int) {
        val transaction = fragmentManager.beginTransaction()
        transaction.add(frameId, fragment)
        transaction.commit()
    }

    fun replaceFragmentToActivity(fragmentManager: FragmentManager,
                                  fragment: Fragment?, frameId: Int) {
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(frameId, fragment)
        transaction.commit()
    }
}
